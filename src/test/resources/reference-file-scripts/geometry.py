from gwcs.geometry import (
    CartesianToSpherical,
    FromDirectionCosines,
    SphericalToCartesian,
    ToDirectionCosines,
)

s2c = SphericalToCartesian(wrap_lon_at=180)
c2s = CartesianToSpherical(wrap_lon_at=180)
to_dc = ToDirectionCosines()
from_dc = FromDirectionCosines()

s2c_inputs = np.array([[45.0, 30.0], [-60.0, 15.0], [120.0, -45.0], [0.0, 0.0]])
s2c_outputs = np.array(
    [list(s2c(row[0], row[1])) for row in s2c_inputs]
)

c2s_inputs = np.array([
    [0.6123724, 0.6123724, 0.5],
    [-0.3535534, 0.6123724, 0.7071068],
    [0.0, -0.7071068, 0.7071068],
    [1.0, 0.0, 0.0],
])
c2s_outputs = np.array(
    [list(c2s(row[0], row[1], row[2])) for row in c2s_inputs]
)

to_dc_inputs = np.array([[0.5, 0.3, 0.0], [1.0, 0.0, 0.0], [0.0, 1.0, 0.0], [-0.5, 0.5, 0.0]])
to_dc_outputs = np.array(
    [list(to_dc(row[0], row[1], row[2])) for row in to_dc_inputs]
)

from_dc_inputs = to_dc_outputs.copy()
from_dc_outputs = np.array(
    [list(from_dc(row[0], row[1], row[2], row[3])) for row in from_dc_inputs]
)

s2c_inv = s2c.inverse
s2c_inv_inputs = s2c_outputs.copy()
s2c_inv_outputs = np.array(
    [list(s2c_inv(row[0], row[1], row[2])) for row in s2c_inv_inputs]
)

c2s_inv = c2s.inverse
c2s_inv_inputs = c2s_outputs.copy()
c2s_inv_outputs = np.array(
    [list(c2s_inv(row[0], row[1])) for row in c2s_inv_inputs]
)

to_dc_inv = to_dc.inverse
to_dc_inv_inputs = to_dc_outputs.copy()
to_dc_inv_outputs = np.array(
    [list(to_dc_inv(row[0], row[1], row[2], row[3])) for row in to_dc_inv_inputs]
)

from_dc_inv = from_dc.inverse
from_dc_inv_inputs = from_dc_outputs.copy()
from_dc_inv_outputs = np.array(
    [list(from_dc_inv(row[0], row[1], row[2])) for row in from_dc_inv_inputs]
)

af["test_cases"] = [
    {
        "name": "spherical_to_cartesian",
        "transform": s2c,
        "forward_inputs": s2c_inputs,
        "forward_outputs": s2c_outputs,
        "has_inverse": True,
        "inverse_inputs": s2c_inv_inputs,
        "inverse_outputs": s2c_inv_outputs,
    },
    {
        "name": "cartesian_to_spherical",
        "transform": c2s,
        "forward_inputs": c2s_inputs,
        "forward_outputs": c2s_outputs,
        "has_inverse": True,
        "inverse_inputs": c2s_inv_inputs,
        "inverse_outputs": c2s_inv_outputs,
    },
    {
        "name": "to_direction_cosines",
        "transform": to_dc,
        "forward_inputs": to_dc_inputs,
        "forward_outputs": to_dc_outputs,
        "has_inverse": True,
        "inverse_inputs": to_dc_inv_inputs,
        "inverse_outputs": to_dc_inv_outputs,
    },
    {
        "name": "from_direction_cosines",
        "transform": from_dc,
        "forward_inputs": from_dc_inputs,
        "forward_outputs": from_dc_outputs,
        "has_inverse": True,
        "inverse_inputs": from_dc_inv_inputs,
        "inverse_outputs": from_dc_inv_outputs,
    },
]
