tabular1d = models.Tabular1D(
    points=np.array([0.0, 1.0, 2.0, 3.0, 4.0, 5.0]),
    lookup_table=np.array([0.0, 2.1, 3.8, 6.2, 7.9, 10.5]),
    method="linear",
    bounds_error=True,
)

tabular1d_inputs = np.array([[0.5], [1.7], [2.3], [4.2]])
tabular1d_outputs = np.array([[tabular1d(row[0])] for row in tabular1d_inputs])

tabular1d_inv = tabular1d.inverse
tabular1d_inv_inputs = tabular1d_outputs.copy()
tabular1d_inv_outputs = np.array(
    [[tabular1d_inv(row[0])] for row in tabular1d_inv_inputs]
)

af["test_cases"] = [
    {
        "name": "tabular1d",
        "transform": tabular1d,
        "forward_inputs": tabular1d_inputs,
        "forward_outputs": tabular1d_outputs,
        "has_inverse": True,
        "inverse_inputs": tabular1d_inv_inputs,
        "inverse_outputs": tabular1d_inv_outputs,
    },
]
