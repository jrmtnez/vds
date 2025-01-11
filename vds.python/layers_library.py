# -*- coding: utf-8 -*-
"""
Created on Tue Mar 24 20:55:58 2015

@author: JuanRicardo
"""

import numpy
import theano
import theano.tensor as T
from theano.tensor.shared_randomstreams import RandomStreams


class LogisticRegressionLayer(object):

    def __init__(self, input, n_in, n_out):

        # matriz de pesos W de dimensión n_in * n_out
        self.W = theano.shared(
                    value = numpy.zeros(
                         (n_in, n_out),
                         dtype = theano.config.floatX),
                    name='W',
                    borrow=True
        )

        # vector de sesgo b de dimensión n_out
        self.b = theano.shared(
                    value = numpy.zeros(
                        (n_out,),
                        dtype = theano.config.floatX),
                    name='b',
                    borrow=True
        )

        # cálculo de la probabilidad de pertenencia a una clase
        self.p_y_given_x = T.nnet.softmax(T.dot(input, self.W) + self.b)

        # recupera la clase cuya probabilidad es máxima
        self.y_pred = T.argmax(self.p_y_given_x, axis=1)

        # almacena los parámetros del modelo
        self.params = [self.W, self.b]

        # se hace visible la capa de entrada para recuperar
        # posteriormente la representación
        self.input25 = input



    def negative_log_likelihood(self, y):

        # devuelve la media de la log-verosimilitud negativa dada una
        # distribución destino y.
        # se utiliza la media y no la suma para que la tasa de aprendizaje
        # sea menos dependiente del tamaño del lote
        return -T.mean(T.log(self.p_y_given_x)[T.arange(y.shape[0]), y])


    def errors(self, y):

        # comprueba que coinciden las dimensiones de y e y_pred
        if y.ndim != self.y_pred.ndim:
            raise TypeError(
                'y must have the same dimension that self.y_pred',
                ('y', y.type, 'y_pred', self.y_pred.type)
            )

        # comprueba el tipo de datos de y
        if y.dtype.startswith('int'):
            # T.neq devuelve un vector de 0s y 1s, donde 1
            # representa un error en la predicción
            return T.mean(T.neq(self.y_pred, y))
        else:
            raise NotImplementedError()


class HiddenLayer(object):

    def __init__(
            self,
            rng,
            input,
            n_in,
            n_out,
            W = None,
            b = None,
            activation = T.tanh):

        self.input = input

        if W is None:
            W_values = numpy.asarray(
                rng.uniform(
                    low = -numpy.sqrt(6. / (n_in + n_out)),
                    high = numpy.sqrt(6. / (n_in + n_out)),
                    size = (n_in, n_out)
                ),
                dtype = theano.config.floatX
            )
            if activation == theano.tensor.nnet.sigmoid:
                W_values *= 4

            W = theano.shared(value = W_values, name = 'W', borrow = True)

        if b is None:
            b_values = numpy.zeros((n_out,), dtype = theano.config.floatX)
            b = theano.shared(value=b_values, name = 'b', borrow = True)

        self.W = W
        self.b = b

        lin_output = T.dot(input, self.W) + self.b
        self.output = (
            lin_output if activation is None
            else activation(lin_output)
        )

        self.params = [self.W, self.b]



class DALayer(object):

    def __init__(
        self,
        numpy_rng,
        theano_rng = None,
        input=None,
        n_visible = 3232,
        n_hidden = 500,
        W = None,
        bhid = None,
        bvis = None
    ):
        self.n_visible = n_visible
        self.n_hidden = n_hidden

        if not theano_rng:
            theano_rng = RandomStreams(numpy_rng.randint(2 ** 30))

        if not W:
            initial_W = numpy.asarray(
                numpy_rng.uniform(
                    low = -4 * numpy.sqrt(6. / (n_hidden + n_visible)),
                    high = 4 * numpy.sqrt(6. / (n_hidden + n_visible)),
                    size = (n_visible, n_hidden)
                ),
                dtype = theano.config.floatX
            )
            W = theano.shared(value = initial_W, name = 'W', borrow = True)

        if not bvis:
            bvis = theano.shared(
                value = numpy.zeros(
                    n_visible,
                    dtype = theano.config.floatX
                ),
                borrow = True
            )

        if not bhid:
            bhid = theano.shared(
                value = numpy.zeros(
                    n_hidden,
                    dtype = theano.config.floatX
                ),
                name = 'b',
                borrow = True
            )

        self.W = W
        self.b = bhid
        self.b_prime = bvis
        self.W_prime = self.W.T
        self.theano_rng = theano_rng

        if input is None:
            self.x = T.dmatrix(name = 'input')
        else:
            self.x = input

        self.params = [self.W, self.b, self.b_prime]


    def get_corrupted_input(self, input, corruption_level):
        return self.theano_rng.binomial(size = input.shape, n = 1,
                                        p = 1 - corruption_level,
                                        dtype = theano.config.floatX) * input

    def get_hidden_values(self, input):
        return T.nnet.sigmoid(T.dot(input, self.W) + self.b)

    def get_reconstructed_input(self, hidden):
        return T.nnet.sigmoid(T.dot(hidden, self.W_prime) + self.b_prime)

    def get_cost_updates(self, corruption_level, learning_rate):

        tilde_x = self.get_corrupted_input(self.x, corruption_level)
        y = self.get_hidden_values(tilde_x)
        z = self.get_reconstructed_input(y)
        L = - T.sum(self.x * T.log(z) + (1 - self.x) * T.log(1 - z), axis = 1)

        cost = T.mean(L)

        gparams = T.grad(cost, self.params)
        updates = [
            (param, param - learning_rate * gparam)
            for param, gparam in zip(self.params, gparams)
        ]

        return (cost, updates)