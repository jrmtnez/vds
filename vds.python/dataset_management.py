# -*- coding: utf-8 -*-
"""
Created on Sat Jun 27 11:39:46 2015

@author: JuanRicardo
"""
import numpy
import theano
import theano.tensor as T

def load_data(training_dataset, validation_dataset, test_dataset):
        
    # carga el dataset con el formato de Vasilev

    train_set_x = numpy.loadtxt('corpus//' + training_dataset + '_input.txt', delimiter=',') 
    train_set_y = numpy.loadtxt('corpus//' + training_dataset + '_target.txt', delimiter=',') 
    valid_set_x = numpy.loadtxt('corpus//' + validation_dataset + '_input.txt', delimiter=',')
    valid_set_y = numpy.loadtxt('corpus//' + validation_dataset + '_target.txt', delimiter=',')
    test_set_x = numpy.loadtxt('corpus//' + test_dataset + '_input.txt', delimiter=',')
    test_set_y = numpy.loadtxt('corpus//' + test_dataset + '_target.txt', delimiter=',')
    
    train_set = (train_set_x, train_set_y)
    valid_set = (valid_set_x, valid_set_y)
    test_set = (test_set_x, test_set_y)

    def shared_dataset(data_xy, borrow = True):
    
        # carga el dataset en variables compartidas para poder almacenarlas
        # en la memoria de la GPU cuando se utiliza ésta
        
        data_x, data_y = data_xy
        
        shared_x = theano.shared(
                        numpy.asarray(data_x, dtype=theano.config.floatX),
                        borrow = borrow)
        
        shared_y = theano.shared(
                        numpy.asarray(data_y, dtype=theano.config.floatX),
                        borrow = borrow)
        
        # la GPU sólo almacena variables float        
        return shared_x, T.cast(shared_y, 'int32')

    test_set_x, test_set_y = shared_dataset(test_set)
    valid_set_x, valid_set_y = shared_dataset(valid_set)
    train_set_x, train_set_y = shared_dataset(train_set)

    rval = [(train_set_x, train_set_y), (valid_set_x, valid_set_y), (test_set_x, test_set_y)]

    return rval