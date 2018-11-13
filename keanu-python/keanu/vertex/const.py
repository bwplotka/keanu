from keanu.tensor import Tensor
from .generated import ConstantBool, ConstantInteger, ConstantDouble
from .base import Vertex

import numpy as np
from keanu.vartypes import int_types, float_types, bool_types, primitive_types, pandas_types

def Const(t) -> Vertex:
    if isinstance(t, np.ndarray):
        ctor = __infer_const_ctor_from_ndarray(t)
        val = t
    elif isinstance(t, pandas_types):
        val = t.values
        ctor = __infer_const_ctor_from_ndarray(val)
    elif isinstance(t, primitive_types):
        ctor = __infer_const_ctor_from_scalar(t)
        val = t
    else:
        raise NotImplementedError("Argument t must be either an ndarray or an instance of numbers.Number. Was given {} instead".format(type(t)))

    return ctor(Tensor(val))

def __infer_const_ctor_from_ndarray(ndarray):
    if isinstance(ndarray, np.ndarray):
        if len(ndarray) == 0:
            raise ValueError("Cannot infer type because the ndarray is empty")

        return __infer_const_ctor_from_scalar(ndarray.item(0))
    else:
        return __infer_const_ctor_from_scalar(ndarray)

def __infer_const_ctor_from_scalar(scalar):
    if isinstance(scalar, bool_types):
        return ConstantBool
    elif isinstance(scalar, int_types):
        return ConstantInteger
    elif isinstance(scalar, float_types):
        return ConstantDouble
    else:
        raise NotImplementedError("Generic types in an ndarray are not supported. Was given {}".format(type(scalar)))
