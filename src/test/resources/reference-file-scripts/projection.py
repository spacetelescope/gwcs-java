gnomonic = models.Pix2Sky_TAN()
stereographic = models.Pix2Sky_STG()
mollweide = models.Pix2Sky_MOL()
healpix = models.Pix2Sky_HPX()

gnomonic_inputs = np.array([[0.0, 0.0], [5.0, 5.0], [-3.0, 7.0], [10.0, -5.0]])
gnomonic_outputs = np.array([list(gnomonic(row[0], row[1])) for row in gnomonic_inputs])

stereographic_inputs = np.array([[0.0, 0.0], [10.0, 10.0], [-5.0, 15.0], [20.0, -10.0]])
stereographic_outputs = np.array(
    [list(stereographic(row[0], row[1])) for row in stereographic_inputs]
)

mollweide_inputs = np.array([[0.0, 0.0], [10.0, 5.0], [-15.0, -8.0], [5.0, 12.0]])
mollweide_outputs = np.array(
    [list(mollweide(row[0], row[1])) for row in mollweide_inputs]
)

healpix_inputs = np.array([[0.0, 0.0], [10.0, 10.0], [-10.0, 20.0], [5.0, -15.0]])
healpix_outputs = np.array(
    [list(healpix(row[0], row[1])) for row in healpix_inputs]
)

gnomonic_inv = gnomonic.inverse
gnomonic_inv_inputs = gnomonic_outputs.copy()
gnomonic_inv_outputs = np.array(
    [list(gnomonic_inv(row[0], row[1])) for row in gnomonic_inv_inputs]
)

stereographic_inv = stereographic.inverse
stereographic_inv_inputs = stereographic_outputs.copy()
stereographic_inv_outputs = np.array(
    [list(stereographic_inv(row[0], row[1])) for row in stereographic_inv_inputs]
)

mollweide_inv = mollweide.inverse
mollweide_inv_inputs = mollweide_outputs.copy()
mollweide_inv_outputs = np.array(
    [list(mollweide_inv(row[0], row[1])) for row in mollweide_inv_inputs]
)

healpix_inv = healpix.inverse
healpix_inv_inputs = healpix_outputs.copy()
healpix_inv_outputs = np.array(
    [list(healpix_inv(row[0], row[1])) for row in healpix_inv_inputs]
)

af["test_cases"] = [
    {
        "name": "gnomonic",
        "transform": gnomonic,
        "forward_inputs": gnomonic_inputs,
        "forward_outputs": gnomonic_outputs,
        "has_inverse": True,
        "inverse_inputs": gnomonic_inv_inputs,
        "inverse_outputs": gnomonic_inv_outputs,
    },
    {
        "name": "stereographic",
        "transform": stereographic,
        "forward_inputs": stereographic_inputs,
        "forward_outputs": stereographic_outputs,
        "has_inverse": True,
        "inverse_inputs": stereographic_inv_inputs,
        "inverse_outputs": stereographic_inv_outputs,
    },
    {
        "name": "mollweide",
        "transform": mollweide,
        "forward_inputs": mollweide_inputs,
        "forward_outputs": mollweide_outputs,
        "has_inverse": True,
        "inverse_inputs": mollweide_inv_inputs,
        "inverse_outputs": mollweide_inv_outputs,
    },
    {
        "name": "healpix",
        "transform": healpix,
        "forward_inputs": healpix_inputs,
        "forward_outputs": healpix_outputs,
        "has_inverse": True,
        "inverse_inputs": healpix_inv_inputs,
        "inverse_outputs": healpix_inv_outputs,
    },
]
