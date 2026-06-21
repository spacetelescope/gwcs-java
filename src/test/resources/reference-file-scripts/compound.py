compose = models.Shift(2.0) | models.Scale(3.0)
concat = models.Shift(1.0) & models.Shift(2.0)
add = models.Shift(1.0) + models.Scale(2.0)
subtract = models.Shift(1.0) - models.Scale(2.0)
multiply = models.Shift(1.0) * models.Scale(2.0)
divide = models.Shift(1.0) / models.Scale(2.0)
power = models.Shift(1.0) ** models.Scale(2.0)
fix = models.fix_inputs(
    models.AffineTransformation2D(
        matrix=np.array([[2.0, 0.0], [0.0, 3.0]]),
        translation=np.array([1.0, 2.0]),
    ),
    {0: 5.0},
)

inputs_1d = np.array([[1.0], [2.5], [-3.0], [0.5]])
inputs_2d = np.array([[1.0, 2.0], [3.0, 4.0], [-1.0, -2.0], [0.0, 0.0]])

compose_outputs = np.array([[compose(x[0])] for x in inputs_1d])
concat_outputs = np.array([list(concat(x[0], x[1])) for x in inputs_2d])
add_outputs = np.array([[add(x[0])] for x in inputs_1d])
subtract_outputs = np.array([[subtract(x[0])] for x in inputs_1d])
multiply_outputs = np.array([[multiply(x[0])] for x in inputs_1d])
divide_outputs = np.array([[divide(x[0])] for x in inputs_1d])
power_outputs = np.array([[power(x[0])] for x in inputs_1d])
fix_outputs = np.array([list(fix(x[0])) for x in inputs_1d])

compose_inv = compose.inverse
compose_inv_inputs = compose_outputs.copy()
compose_inv_outputs = np.array([[compose_inv(x[0])] for x in compose_inv_inputs])

concat_inv = concat.inverse
concat_inv_inputs = concat_outputs.copy()
concat_inv_outputs = np.array(
    [list(concat_inv(x[0], x[1])) for x in concat_inv_inputs]
)

af["test_cases"] = [
    {
        "name": "compose",
        "transform": compose,
        "forward_inputs": inputs_1d,
        "forward_outputs": compose_outputs,
        "has_inverse": True,
        "inverse_inputs": compose_inv_inputs,
        "inverse_outputs": compose_inv_outputs,
    },
    {
        "name": "concatenate",
        "transform": concat,
        "forward_inputs": inputs_2d,
        "forward_outputs": concat_outputs,
        "has_inverse": True,
        "inverse_inputs": concat_inv_inputs,
        "inverse_outputs": concat_inv_outputs,
    },
    {
        "name": "add",
        "transform": add,
        "forward_inputs": inputs_1d,
        "forward_outputs": add_outputs,
        "has_inverse": False,
    },
    {
        "name": "subtract",
        "transform": subtract,
        "forward_inputs": inputs_1d,
        "forward_outputs": subtract_outputs,
        "has_inverse": False,
    },
    {
        "name": "multiply",
        "transform": multiply,
        "forward_inputs": inputs_1d,
        "forward_outputs": multiply_outputs,
        "has_inverse": False,
    },
    {
        "name": "divide",
        "transform": divide,
        "forward_inputs": inputs_1d,
        "forward_outputs": divide_outputs,
        "has_inverse": False,
    },
    {
        "name": "power",
        "transform": power,
        "forward_inputs": inputs_1d,
        "forward_outputs": power_outputs,
        "has_inverse": False,
    },
    {
        "name": "fix_inputs",
        "transform": fix,
        "forward_inputs": inputs_1d,
        "forward_outputs": fix_outputs,
        "has_inverse": False,
    },
]
