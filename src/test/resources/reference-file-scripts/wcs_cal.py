detector = cf.Frame2D(name="detector", axes_order=(0, 1))
v2v3 = cf.Frame2D(name="v2v3", axes_order=(0, 1))
v2v3vacorr = cf.Frame2D(name="v2v3vacorr", axes_order=(0, 1))
v2v3corr = cf.Frame2D(name="v2v3corr", axes_order=(0, 1))
sky = cf.CelestialFrame(reference_frame=coord.ICRS(), axes_order=(0, 1))

distortion = (
    (models.Shift(-1024.0) & models.Shift(-1024.0))
    | models.AffineTransformation2D(
        matrix=np.array([[0.11, 0.005], [-0.005, 0.11]]),
        translation=np.array([0.0, 0.0]),
    )
)

velocity_correction = models.AffineTransformation2D(
    matrix=np.array([[1.001, 0.0], [0.0, 0.999]]),
    translation=np.array([0.0, 0.0]),
)

tpc_correction = models.AffineTransformation2D(
    matrix=np.array([[1.0, 0.002], [-0.002, 1.0]]),
    translation=np.array([0.1, -0.05]),
)

tan = models.Pix2Sky_TAN()
celestial_rotation = models.RotateNative2Celestial(
    lon=53.16, lat=-27.79, lon_pole=180.0
)
to_sky = (
    (models.Scale(1.0 / 3600.0) & models.Scale(1.0 / 3600.0))
    | tan
    | celestial_rotation
)

pipeline = [
    (detector, distortion),
    (v2v3, velocity_correction),
    (v2v3vacorr, tpc_correction),
    (v2v3corr, to_sky),
    (sky, None),
]
w = gwcs_wcs.WCS(pipeline)

inputs = np.array([
    [1024.0, 1024.0],
    [512.0, 512.0],
    [100.0, 200.0],
    [1500.0, 1800.0],
])
outputs = np.array([list(w(row[0], row[1])) for row in inputs])

inv = w.backward_transform
inv_inputs = outputs.copy()
inv_outputs = np.array([list(inv(row[0], row[1])) for row in inv_inputs])

af["test_cases"] = [
    {
        "name": "cal_pipeline",
        "wcs": w,
        "forward_inputs": inputs,
        "forward_outputs": outputs,
        "has_inverse": True,
        "inverse_inputs": inv_inputs,
        "inverse_outputs": inv_outputs,
        "tolerance": 1e-9,
    },
]
