shift = models.Shift(3.0)
scale = models.Scale(2.5)
affine = models.AffineTransformation2D(
    matrix=np.array([[0.9, -0.1], [0.2, 1.1]]),
    translation=np.array([5.0, -3.0]),
)

shift_inputs = np.array([[1.0], [2.5], [-3.0], [0.0]])
shift_outputs = np.array([[x[0] + 3.0] for x in shift_inputs])

scale_inputs = np.array([[1.0], [2.5], [-3.0], [0.0]])
scale_outputs = np.array([[x[0] * 2.5] for x in scale_inputs])

affine_inputs = np.array([[1.0, 2.0], [0.0, 0.0], [-1.5, 3.5], [10.0, -5.0]])
affine_outputs = np.array([affine(x[0], x[1]) for x in affine_inputs])

shift_inv_inputs = shift_outputs.copy()
shift_inv_outputs = shift_inputs.copy()

scale_inv_inputs = scale_outputs.copy()
scale_inv_outputs = scale_inputs.copy()

affine_inv = affine.inverse
affine_inv_inputs = affine_outputs.copy()
affine_inv_outputs = np.array([affine_inv(x[0], x[1]) for x in affine_inv_inputs])

af["test_cases"] = [
    {
        "name": "shift",
        "transform": shift,
        "forward_inputs": shift_inputs,
        "forward_outputs": shift_outputs,
        "has_inverse": True,
        "inverse_inputs": shift_inv_inputs,
        "inverse_outputs": shift_inv_outputs,
    },
    {
        "name": "scale",
        "transform": scale,
        "forward_inputs": scale_inputs,
        "forward_outputs": scale_outputs,
        "has_inverse": True,
        "inverse_inputs": scale_inv_inputs,
        "inverse_outputs": scale_inv_outputs,
    },
    {
        "name": "affine",
        "transform": affine,
        "forward_inputs": affine_inputs,
        "forward_outputs": affine_outputs,
        "has_inverse": True,
        "inverse_inputs": affine_inv_inputs,
        "inverse_outputs": affine_inv_outputs,
    },
]
