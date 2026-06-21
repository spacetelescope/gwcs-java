crpix = np.array([512.0, 512.0])
crval = np.array([180.0, 45.0])
cdelt = np.array([-1e-4, 1e-4])
pc = np.array([[1.0, 0.0], [0.0, 1.0]])
cd = np.outer(cdelt, np.array([1.0, 1.0])) * pc

shift_x = models.Shift(-crpix[0])
shift_y = models.Shift(-crpix[1])
affine = models.AffineTransformation2D(matrix=cd)
tan = models.Pix2Sky_TAN()
rot = models.RotateNative2Celestial(crval[0], crval[1], 180.0)

transform = (shift_x & shift_y) | affine | tan | rot

forward_inputs = np.array([
    [512.0, 512.0],
    [0.0, 0.0],
    [1023.0, 1023.0],
    [256.0, 768.0],
])
forward_outputs = np.array(
    [list(transform(row[0], row[1])) for row in forward_inputs]
)

inv = transform.inverse
inverse_inputs = forward_outputs.copy()
inverse_outputs = np.array(
    [list(inv(row[0], row[1])) for row in inverse_inputs]
)

af["test_cases"] = [
    {
        "name": "fits_wcs_imaging",
        "transform": transform,
        "forward_inputs": forward_inputs,
        "forward_outputs": forward_outputs,
        "has_inverse": True,
        "inverse_inputs": inverse_inputs,
        "inverse_outputs": inverse_outputs,
        "tolerance": 1e-5,
    },
]
