from gwcs import spectroscopy as sp

sellmeier_glass = sp.SellmeierGlass(
    B_coef=[1.03961212, 0.23179234, 1.01046945],
    C_coef=[6.00069867e-3, 2.00179144e-2, 1.03560653e2],
)

glass_inputs = np.array([[0.5], [0.6], [0.8], [1.0]])
glass_outputs = np.array([[sellmeier_glass(row[0])] for row in glass_inputs])

sellmeier_zemax = sp.SellmeierZemax(
    temperature=22.0,
    ref_temperature=20.0,
    ref_pressure=101325.0,
    pressure=101325.0,
    B_coef=[1.03961212, 0.23179234, 1.01046945],
    C_coef=[6.00069867e-3, 2.00179144e-2, 1.03560653e2],
    D_coef=[1.86e-6, 1.31e-8, -1.37e-11],
    E_coef=[4.34e-7, 1.15e-9, 0.17],
)

zemax_inputs = np.array([[0.5], [0.6], [0.8], [1.0]])
zemax_outputs = np.array(
    [[sellmeier_zemax(np.array([row[0]]))[0]] for row in zemax_inputs]
)

snell3d = sp.Snell3D()

snell_inputs = np.array([
    [1.5, 0.3, 0.4, 0.0],
    [1.0, 0.3, 0.4, 0.0],
    [1.2, 0.1, 0.2, 0.0],
    [2.0, 0.5, 0.3, 0.0],
])
snell_outputs = np.array(
    [list(snell3d(row[0], row[1], row[2], row[3])) for row in snell_inputs]
)

angles_from_grating = sp.AnglesFromGratingEquation3D(
    groove_density=2700.0, spectral_order=-1,
)

angles_inputs = np.array([
    [2e-6, 0.1, 0.1],
    [1e-6, 0.05, 0.05],
    [5e-7, 0.2, 0.1],
    [3e-6, 0.01, 0.02],
])
angles_outputs = np.array(
    [list(angles_from_grating(row[0], row[1], row[2])) for row in angles_inputs]
)

wavelength_from_grating = sp.WavelengthFromGratingEquation(
    groove_density=2700.0, spectral_order=-1,
)

wl_inputs = np.array([
    [0.1, 0.2],
    [0.05, 0.15],
    [0.3, 0.1],
    [0.01, 0.02],
])
wl_outputs = np.array(
    [[wavelength_from_grating(row[0], row[1])] for row in wl_inputs]
)

af["test_cases"] = [
    {
        "name": "sellmeier_glass",
        "transform": sellmeier_glass,
        "forward_inputs": glass_inputs,
        "forward_outputs": glass_outputs,
        "has_inverse": False,
    },
    {
        "name": "sellmeier_zemax",
        "transform": sellmeier_zemax,
        "forward_inputs": zemax_inputs,
        "forward_outputs": zemax_outputs,
        "has_inverse": False,
    },
    {
        "name": "snell3d",
        "transform": snell3d,
        "forward_inputs": snell_inputs,
        "forward_outputs": snell_outputs,
        "has_inverse": False,
    },
    {
        "name": "angles_from_grating_equation_3d",
        "transform": angles_from_grating,
        "forward_inputs": angles_inputs,
        "forward_outputs": angles_outputs,
        "has_inverse": False,
    },
    {
        "name": "wavelength_from_grating_equation",
        "transform": wavelength_from_grating,
        "forward_inputs": wl_inputs,
        "forward_outputs": wl_outputs,
        "has_inverse": False,
    },
]
