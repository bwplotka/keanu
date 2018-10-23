from py4j.java_gateway import java_import
from keanu.base import JavaObjectWrapper, JavaCtor, JavaList, JavaSet, UnaryLambda
from keanu.context import KeanuContext
import numpy as np

context = KeanuContext()
k = context.jvm_view()


java_import(k, "io.improbable.keanu.network.BayesianNetwork")
class BayesNet(JavaCtor):
    def __init__(self, lst):
        if isinstance(lst, (JavaList, JavaSet)):
            vertices = lst.unwrap()
        elif isinstance(lst, list):
            vertices = context.to_java_list([vertex.unwrap() for vertex in lst])
        else:
            raise ValueError("Expected a list. Was given {}".format(type(lst)))

        super(BayesNet, self).__init__(k.BayesianNetwork, vertices)

    def get_latent_or_observed_vertices(self):
        return JavaList(self.unwrap().getLatentOrObservedVertices())

    def get_latent_vertices(self):
        return JavaList(self.unwrap().getLatentVertices())

    def get_observed_vertices(self):
        return JavaList(self.unwrap().getObservedVertices())


class VertexSamples(JavaObjectWrapper):
    def __init__(self, vertex_samples):
        super(VertexSamples, self).__init__(vertex_samples)

    def probability(self, fn):
        return self.unwrap().probability(UnaryLambda(fn))

    def get_averages(self):
        keanu_tensor = self.unwrap().getAverages()
        return self.__to_np_array(keanu_tensor)

    def as_list(self):
        return JavaList(self.unwrap().asList())

    def __to_np_array(self, value):
        np_array = np.array(list(value.asFlatArray()))
        return np_array.reshape(value.getShape())


class NetworkSamples(JavaObjectWrapper):
    def __init__(self, network_samples):
        super(NetworkSamples, self).__init__(network_samples)

    def get_double_tensor_samples(self, vertex):
        return VertexSamples(self.unwrap().getDoubleTensorSamples(vertex.unwrap()))

    def get_integer_tensor_samples(self, vertex):
        return VertexSamples(self.unwrap().getIntegerTensorSamples(vertex.unwrap()))

    def get(self, vertex):
        return VertexSamples(self.unwrap().get(vertex.unwrap()))

    def drop(self, drop_count):
        return NetworkSamples(self.unwrap().drop(drop_count))

    def down_sample(self, down_sample_interval):
        return NetworkSamples(self.unwrap().downSample(down_sample_interval))


class InferenceAlgorithm:
    def __init__(self, algorithm):
        self.algorithm = algorithm

    def get_posterior_samples(self, net, lst, sample_count):
        if isinstance(lst, JavaList):
            vertices = lst.unwrap()
        elif isinstance(lst, list):
            vertices = context.to_java_list([vertex.unwrap() for vertex in lst])
        else:
            raise ValueError("Expected a list. Was given {}".format(type(lst)))

        network_samples = self.algorithm.withDefaultConfig().getPosteriorSamples(
            net.unwrap(),
            vertices,
            sample_count)

        return NetworkSamples(network_samples)


java_import(k, "io.improbable.keanu.algorithms.mcmc.MetropolisHastings")
class MetropolisHastings(InferenceAlgorithm):
    def __init__(self):
        super(MetropolisHastings, self).__init__(k.MetropolisHastings)


java_import(k, "io.improbable.keanu.algorithms.mcmc.NUTS")
class NUTS(InferenceAlgorithm):
    def __init__(self):
        super(NUTS, self).__init__(k.NUTS)


java_import(k, "io.improbable.keanu.algorithms.mcmc.Hamiltonian")
class Hamiltonian(InferenceAlgorithm):
    def __init__(self):
        super(Hamiltonian, self).__init__(k.Hamiltonian)
