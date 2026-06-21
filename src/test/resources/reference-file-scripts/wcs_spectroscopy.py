from gwcs import spectroscopy as sp

grating = sp.WavelengthFromGratingEquation(groove_density=2700.0, spectral_order=-1)
glass = sp.SellmeierGlass(
    B_coef=[1.03961212, 0.23179234, 1.01046945],
    C_coef=[6.00069867e-3, 2.00179144e-2, 1.03560653e2],
)

grating_inputs = np.array([
    [0.1, 0.2],
    [0.05, 0.15],
    [-0.1, 0.3],
    [0.15, -0.1],
])
grating_outputs = np.array(
    [[grating(row[0], row[1])] for row in grating_inputs]
)

glass_inputs = np.array([
    [0.5],
    [0.6],
    [0.7],
    [0.8],
])
glass_outputs = np.array(
    [[glass(row[0])] for row in glass_inputs]
)

af["test_cases"] = [
    {
        "name": "wavelength_from_grating_equation",
        "transform": grating,
        "forward_inputs": grating_inputs,
        "forward_outputs": grating_outputs,
        "has_inverse": False,
    },
    {
        "name": "sellmeier_glass",
        "transform": glass,
        "forward_inputs": glass_inputs,
        "forward_outputs": glass_outputs,
        "has_inverse": False,
    },
]
