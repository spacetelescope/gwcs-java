import os
import asdf

script_dir = os.path.dirname(os.path.abspath(__file__)) if '__file__' in dir() else '.'
resource_dir = os.path.join(script_dir, '..', 'roman-reference-files')
if not os.path.isdir(resource_dir):
    resource_dir = os.path.join('src', 'test', 'resources', 'roman-reference-files')

test_cases = []

CAL_FILENAME = 'r0034001001001001001_0001_wfi01_f062_cal_wcs_only.asdf'
COADD_FILENAME = 'r0000101001001001001_p_v01001001001001_045p86x47y51_f158_coadd_wcs_only.asdf'

cal_path = os.path.join(resource_dir, CAL_FILENAME)
if os.path.exists(cal_path):
    with asdf.open(cal_path) as cal_asdf:
        cal_wcs = cal_asdf['roman']['meta']['wcs']

        cal_inputs = np.array([
            [1024.0, 1024.0],
            [0.0, 0.0],
            [2047.0, 2047.0],
            [512.0, 1536.0],
        ])
        cal_outputs = np.array([list(cal_wcs(row[0], row[1])) for row in cal_inputs])

        cal_inv = cal_wcs.backward_transform
        cal_inv_inputs = cal_outputs.copy()
        cal_inv_outputs = np.array(
            [list(cal_inv(row[0], row[1])) for row in cal_inv_inputs]
        )

        test_cases.append({
            "name": "roman_cal",
            "fixture_path": "roman-reference-files/" + CAL_FILENAME,
            "forward_inputs": cal_inputs,
            "forward_outputs": cal_outputs,
            "has_inverse": True,
            "inverse_inputs": cal_inv_inputs,
            "inverse_outputs": cal_inv_outputs,
            "tolerance": 1e-10,
        })

coadd_path = os.path.join(resource_dir, COADD_FILENAME)
if os.path.exists(coadd_path):
    with asdf.open(coadd_path) as coadd_asdf:
        coadd_wcs = coadd_asdf['roman']['meta']['wcs']

        coadd_inputs = np.array([
            [512.0, 512.0],
            [0.0, 0.0],
            [1023.0, 1023.0],
            [256.0, 768.0],
        ])
        coadd_outputs = np.array(
            [list(coadd_wcs(row[0], row[1])) for row in coadd_inputs]
        )

        coadd_inv = coadd_wcs.backward_transform
        coadd_inv_inputs = coadd_outputs.copy()
        coadd_inv_outputs = np.array(
            [list(coadd_inv(row[0], row[1])) for row in coadd_inv_inputs]
        )

        test_cases.append({
            "name": "roman_coadd",
            "fixture_path": "roman-reference-files/" + COADD_FILENAME,
            "forward_inputs": coadd_inputs,
            "forward_outputs": coadd_outputs,
            "has_inverse": True,
            "inverse_inputs": coadd_inv_inputs,
            "inverse_outputs": coadd_inv_outputs,
            "tolerance": 1e-10,
        })

af["test_cases"] = test_cases
