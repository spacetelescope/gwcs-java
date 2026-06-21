poly1d = models.Polynomial1D(
    degree=3, c0=1.0, c1=-2.0, c2=0.5, c3=0.1, domain=[-5, 5], window=[-1, 1]
)
poly2d = models.Polynomial2D(
    degree=2, c0_0=1.0, c1_0=2.0, c0_1=-1.0, c2_0=0.3, c1_1=0.5, c0_2=-0.2
)

poly1d_inputs = np.array([[0.0], [2.5], [-3.0], [4.5]])
poly1d_outputs = np.array([[poly1d(row[0])] for row in poly1d_inputs])

poly2d_inputs = np.array([[1.0, 2.0], [0.0, 0.0], [-1.5, 3.5], [2.0, -1.0]])
poly2d_outputs = np.array([[poly2d(row[0], row[1])] for row in poly2d_inputs])

af["test_cases"] = [
    {
        "name": "polynomial1d",
        "transform": poly1d,
        "forward_inputs": poly1d_inputs,
        "forward_outputs": poly1d_outputs,
        "has_inverse": False,
    },
    {
        "name": "polynomial2d",
        "transform": poly2d,
        "forward_inputs": poly2d_inputs,
        "forward_outputs": poly2d_outputs,
        "has_inverse": False,
    },
]
