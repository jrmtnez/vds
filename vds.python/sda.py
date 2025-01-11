# -*- coding: utf-8 -*-
"""
Created on Tue Mar 24 20:55:58 2015

@author: JuanRicardo
"""

import time
import sys
import numpy

import theano
import theano.tensor as T
from theano.tensor.shared_randomstreams import RandomStreams

from datasetdataset_management import load_data
from layers_library import LogisticRegressionLayer, HiddenLayer, DALayer


class SdA(object):

    def __init__(
        self,
        numpy_rng,
        theano_rng = None,
        n_ins = 0,
        hidden_layers_sizes = [],
        n_outs = 0,
        corruption_levels = [0.1, 0.1]
    ):

        self.sigmoid_layers = []
        self.dA_layers = []
        self.params = []
        self.n_layers = len(hidden_layers_sizes)

        assert self.n_layers > 0

        if not theano_rng:
            theano_rng = RandomStreams(numpy_rng.randint(2 ** 30))

        self.x = T.matrix('x')
        self.y = T.ivector('y')

        for i in xrange(self.n_layers):

            if i == 0:
                input_size = n_ins
            else:
                input_size = hidden_layers_sizes[i - 1]

            if i == 0:
                layer_input = self.x
            else:
                layer_input = self.sigmoid_layers[-1].output

            sigmoid_layer = HiddenLayer(rng = numpy_rng,
                                        input = layer_input,
                                        n_in = input_size,
                                        n_out = hidden_layers_sizes[i],
                                        activation = T.nnet.sigmoid)

            self.sigmoid_layers.append(sigmoid_layer)
            self.params.extend(sigmoid_layer.params)

            dA_layer = DALayer(numpy_rng = numpy_rng,
                          theano_rng = theano_rng,
                          input = layer_input,
                          n_visible = input_size,
                          n_hidden = hidden_layers_sizes[i],
                          W = sigmoid_layer.W,
                          bhid = sigmoid_layer.b)

            self.dA_layers.append(dA_layer)

        self.logLayer = LogisticRegressionLayer(
                            input = self.sigmoid_layers[-1].output,
                            n_in = hidden_layers_sizes[-1],
                            n_out = n_outs
        )

        self.params.extend(self.logLayer.params)
        self.finetune_cost = self.logLayer.negative_log_likelihood(self.y)
        self.errors = self.logLayer.errors(self.y)


    def pretraining_functions(self, train_set_x, batch_size):

        index = T.lscalar('index')
        corruption_level = T.scalar('corruption')
        learning_rate = T.scalar('lr')
        batch_begin = index * batch_size
        batch_end = batch_begin + batch_size

        pretrain_fns = []
        for dAl in self.dA_layers:

            cost, updates = dAl.get_cost_updates(corruption_level,
                                                learning_rate)
            fn = theano.function(
                inputs = [
                    index,
                    theano.Param(corruption_level, default = 0.2),
                    theano.Param(learning_rate, default = 0.1)
                ],
                outputs = cost,
                updates = updates,
                givens = {
                    self.x: train_set_x[batch_begin: batch_end]
                }
            )

            pretrain_fns.append(fn)

        return pretrain_fns

    def build_finetune_functions(self, datasets, batch_size, learning_rate):

        (train_set_x, train_set_y) = datasets[0]
        (valid_set_x, valid_set_y) = datasets[1]
        (test_set_x, test_set_y) = datasets[2]

        n_valid_batches = valid_set_x.get_value(borrow = True).shape[0]
        n_valid_batches /= batch_size
        n_test_batches = test_set_x.get_value(borrow = True).shape[0]
        n_test_batches /= batch_size

        index = T.lscalar('index')


        gparams = T.grad(self.finetune_cost, self.params)

        updates = [
            (param, param - gparam * learning_rate)
            for param, gparam in zip(self.params, gparams)
        ]

        train_fn = theano.function(
            inputs = [index],
            outputs = self.finetune_cost,
            updates = updates,
            givens = {
                self.x: train_set_x[
                    index * batch_size: (index + 1) * batch_size
                ],
                self.y: train_set_y[
                    index * batch_size: (index + 1) * batch_size
                ]
            },
            name = 'train'
        )

        test_score_i = theano.function(
            [index],
            self.errors,
            givens = {
                self.x: test_set_x[
                    index * batch_size: (index + 1) * batch_size
                ],
                self.y: test_set_y[
                    index * batch_size: (index + 1) * batch_size
                ]
            },
            name = 'test'
        )

        valid_score_i = theano.function(
            [index],
            self.errors,
            givens = {
                self.x: valid_set_x[
                    index * batch_size: (index + 1) * batch_size
                ],
                self.y: valid_set_y[
                    index * batch_size: (index + 1) * batch_size
                ]
            },
            name = 'valid'
        )


        def valid_score():
            return [valid_score_i(i) for i in xrange(n_valid_batches)]


        def test_score():
            return [test_score_i(i) for i in xrange(n_test_batches)]


        train_fn2 = theano.function(
            inputs = [index],
            outputs = [self.y, self.logLayer.y_pred],
            givens = {
                self.x: train_set_x[index : (index + 1)],
                self.y: train_set_y[index : (index + 1)]
            }
        )

        test_fn2 = theano.function(
            inputs = [index],
            outputs = [self.y, self.logLayer.y_pred],
            givens = {
                self.x: test_set_x[index : (index + 1)],
                self.y: test_set_y[index : (index + 1)]
            }
        )

        train_repr_fn2 = theano.function(
            inputs = [index],
            outputs = [self.y, self.logLayer.input25],
            givens = {
                self.x: train_set_x[index : (index + 1)],
                self.y: train_set_y[index : (index + 1)]
            }
        )

        test_repr_fn2 = theano.function(
            inputs = [index],
            outputs = [self.y, self.logLayer.input25],
            givens = {
                self.x: test_set_x[index : (index + 1)],
                self.y: test_set_y[index : (index + 1)]
            }
        )

        return train_fn, train_fn2, test_fn2, train_repr_fn2, test_repr_fn2, valid_score, test_score


def test_SDA(
        finetune_lr = 0.1,
        pretraining_epochs = 15,
        pretrain_lr = 0.001,
        training_epochs = 100,
        batch_size = 10,
        n_ins = 0,
        hidden_layers_sizes = [],
        n_outs = 0,
        dataset_1 = '',
        dataset_2 = '',
        dataset_3 = ''
        ):


    datasets = load_data(dataset_1, dataset_2, dataset_3)

    train_set_x, train_set_y = datasets[0]
    valid_set_x, valid_set_y = datasets[1]
    test_set_x, test_set_y = datasets[2]

    n_train_instances = train_set_x.get_value(borrow = True).shape[0]
    n_test_instances = test_set_x.get_value(borrow = True).shape[0]

    print 'Training instances: ' + str(n_train_instances)
    print 'Test instances: ' + str(n_test_instances)


    n_train_batches = n_train_instances / batch_size


    numpy_rng = numpy.random.RandomState(89677)
    print 'Building model...'

    sda = SdA(
        numpy_rng = numpy_rng,
        n_ins = n_ins,
        hidden_layers_sizes = hidden_layers_sizes,
        n_outs = n_outs
    )

    print 'Getting pretraining functions...'

    pretraining_fns = sda.pretraining_functions(train_set_x = train_set_x,
                                                batch_size = batch_size)

    print 'Pretraining model...'

    start_time = time.clock()


    corruption_levels = [.1, .2, .3]
    for i in xrange(sda.n_layers):

        for epoch in xrange(pretraining_epochs):

            c = []
            for batch_index in xrange(n_train_batches):
                c.append(pretraining_fns[i](index = batch_index,
                         corruption = corruption_levels[i],
                         lr = pretrain_lr))

            print 'Pretraining layer %i, epoch %d, cost ' % (i, epoch),
            print numpy.mean(c)


    end_time = time.clock()
    print >> sys.stderr, ('Pretraining execution time %.1fs' %
        ((end_time - start_time)))

    print 'Getting fine tune functions...'

    train_fn, train_fn2, test_fn2, train_repr_fn2, test_repr_fn2, validate_model, test_model = sda.build_finetune_functions(
        datasets = datasets,
        batch_size = batch_size,
        learning_rate = finetune_lr
    )

    print 'Fine tune model...'

    patience = 10 * n_train_batches
    patience_increase = 2.
    improvement_threshold = 0.995
    validation_frequency = min(n_train_batches, patience / 2)

    best_validation_loss = numpy.inf
    test_score = 0.
    start_time = time.clock()

    done_looping = False
    epoch = 0

    while (epoch < training_epochs) and (not done_looping):
        epoch = epoch + 1
        for minibatch_index in xrange(n_train_batches):
            minibatch_avg_cost = train_fn(minibatch_index)
            iter = (epoch - 1) * n_train_batches + minibatch_index

            if (iter + 1) % validation_frequency == 0:
                validation_losses = validate_model()
                this_validation_loss = numpy.mean(validation_losses)

                print 'Epoch %i, validation set error %f %%' % (
                    epoch, this_validation_loss * 100.)

                if this_validation_loss < best_validation_loss:

                    if (
                        this_validation_loss < best_validation_loss *
                        improvement_threshold
                    ):
                        patience = max(patience, iter * patience_increase)

                    best_validation_loss = this_validation_loss

                    test_losses = test_model()
                    test_score = numpy.mean(test_losses)

                    print 'Best result. Test set error %f %%' % (
                        test_score * 100.)

            if patience <= iter:
                done_looping = True
                break

    end_time = time.clock()

    print
    print 'Optimizing complete!!!'
    print 'Best validation result: %f %%' % (best_validation_loss * 100.)
    print 'Best test result: %f %%' % (test_score * 100.)
    print

    print '%d epochs executed, %f epochs/sec' % (
        epoch, 1. * epoch / (end_time - start_time))


    print >> sys.stderr, ('Fine tune execution time %.1fs' %
        ((end_time - start_time)))


    file = open("sda_train25.arff", "w")

    file.write("@relation sda_train25\n")
    file.write("@attribute vandalism {0,1}\n")
    for feature in range(25) :
        file.write("@attribute f" + str(feature) + " numeric\n")

    file.write("@data\n")
    n_train_instances = train_set_x.get_value(borrow = True).shape[0]
    for instance_index in xrange(n_train_instances) :

        print "Training instance: " + str(instance_index + 1) + "/" + str(n_train_instances)

        ry1, r_repres1 = train_repr_fn2(instance_index)

        print ry1
        print r_repres1[0]

#        line = str(instance_index + 1) + "," + str(ry1[0])
        line = str(ry1[0])
        for element_index in range(r_repres1[0].shape[0]) :
            element = str(r_repres1[0][element_index])
            line = line + "," + element


        file.write(line + "\n")

    file.close()


    file = open("sda_test25.arff", "w")
    file.write("@relation sda_test25\n")
    file.write("@attribute vandalism {0,1}\n")
    for feature in range(25) :
        file.write("@attribute f" + str(feature) + " numeric\n")

    file.write("@data\n")

    n_test_instances = test_set_x.get_value(borrow = True).shape[0]
    for instance_index in xrange(n_test_instances):

        print "Test instance: " + str(instance_index + 1) + "/" + str(n_test_instances)

        ry2, r_repres2 = test_repr_fn2(instance_index)

        print ry2
        print r_repres2[0]

#        line = str(instance_index + 1) + "," + str(ry2[0])
        line = str(ry2[0])
        for element_index in range(r_repres2[0].shape[0]) :
            element = str(r_repres2[0][element_index])
            line = line + "," + element

        file.write(line + "\n")

    file.close()


    print
    print 'Calculating confusion matrix...'
    print

    tp1 = 0
    tn1 = 0
    fp1 = 0
    fn1 = 0

    tp2 = 0
    tn2 = 0
    fp2 = 0
    fn2 = 0


    file = open("sda_train2.arff", "w")
    file.write("@relation sda_train2\n")
    file.write("@attribute pred numeric\n")
    file.write("@attribute vandalism {0,1}\n")
    file.write("@data\n")

    for instance_index in xrange(n_train_instances):

        ry1, ry_pred1 = train_fn2(instance_index)

        #print 'train: y = %i, y_pred = %i' % (ry1, ry_pred1)

        if ry1 == ry_pred1 and ry1 == 1:
            tp1 = tp1 + 1

        if ry1 == ry_pred1 and ry1 == 0:
            tn1 = tn1 + 1

        if ry1 <> ry_pred1 and ry1 == 1:
            fn1 = fn1  + 1

        if ry1 <> ry_pred1 and ry1 == 0:
            fp1 = fp1 + 1

        file.write(str(ry_pred1) + "," + str(ry1) + "\n")

    file.close()


    file = open("sda_test2.arff", "w")
    file.write("@relation sda_test2\n")
    file.write("@attribute pred numeric\n")
    file.write("@attribute vandalism {0,1}\n")
    file.write("@data\n")

    for instance_index in xrange(n_test_instances):

        ry2, ry_pred2 = test_fn2(instance_index)

        #print 'test: y = %i, y_pred = %i' % (ry2, ry_pred2)

        if ry2 == ry_pred2 and ry2 == 1:
            tp2 = tp2 + 1

        if ry2 == ry_pred2 and ry2 == 0:
            tn2 = tn2 + 1

        if ry2 <> ry_pred2 and ry2 == 1:
            fn2 = fn2  + 1

        if ry2 <> ry_pred2 and ry2 == 0:
            fp2 = fp2 + 1

        file.write(str(ry_pred2) + "," + str(ry2) + "\n")

    file.close()

    print 'Train set'
    print 'tp = %i' % tp1
    print 'fn = %i' % fn1
    print 'fp = %i' % fp1
    print 'tn = %i' % tn1
    print
    print 'Test set'
    print 'tp = %i' % tp2
    print 'fn = %i' % fn2
    print 'fp = %i' % fp2
    print 'tn = %i' % tn2

    print
    print 'Train set: %s' % dataset_1
    print 'Validation set: %s' % dataset_2
    print 'Test set: %s' % dataset_3
    print 'Pretraining epochs: %i' % pretraining_epochs
    print 'Pretraining learning rate: %f' % pretrain_lr
    print 'Fine tune epochs: %i' % training_epochs
    print 'Fine tune learning rate: %f' % finetune_lr
    print 'Lot size: %i' % batch_size
    print 'Hidden layers: %s' % hidden_layers_sizes
    print


if __name__ == '__main__':

    test_SDA(
        finetune_lr = 0.1,
        pretraining_epochs = 5,
        pretrain_lr = 0.001,
        training_epochs = 1,
        batch_size = 10,
        dataset_1 = 'inserted_deleted_vemodel_training',
        dataset_2 = 'inserted_deleted_vemodel_training',
        dataset_3 = 'inserted_deleted_vemodel_test',
        n_ins = 10050,
        hidden_layers_sizes = [500, 100, 25],
        n_outs = 2
        )
