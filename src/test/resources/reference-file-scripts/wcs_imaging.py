detector = cf.Frame2D(name="detector", axes_order=(0, 1))
sky = cf.CelestialFrame(reference_frame=coord.ICRS(), axes_order=(0, 1))

shift = models.Shift(1.0) & models.Shift(2.0)
scale = models.Scale(0.1) & models.Scale(0.1)
transform = shift | scale

pipeline = [(detector, transform), (sky, None)]
w = gwcs_wcs.WCS(pipeline)

inputs = np.array([[0.0, 0.0], [10.0, 20.0], [50.0, 50.0], [100.0, 100.0]])
outputs = np.array([list(w(row[0], row[1])) for row in inputs])

inv = w.backward_transform
inv_inputs = outputs.copy()
inv_outputs = np.array([list(inv(row[0], row[1])) for row in inv_inputs])

af["test_cases"] = [
    {
        "name": "simple_imaging",
        "wcs": w,
        "forward_inputs": inputs,
        "forward_outputs": outputs,
        "has_inverse": True,
        "inverse_inputs": inv_inputs,
        "inverse_outputs": inv_outputs,
    },
]
