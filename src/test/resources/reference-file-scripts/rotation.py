rotation2d = models.Rotation2D(angle=35.0)

euler_angle = models.EulerAngleRotation(23.0, 45.0, 67.0, axes_order='xyz')

rotate_seq = models.RotationSequence3D(angles=[10.0, 20.0, 30.0], axes_order='xyz')

native2celestial = models.RotateNative2Celestial(lon=45.0, lat=60.0, lon_pole=180.0)

celestial2native = models.RotateCelestial2Native(lon=45.0, lat=60.0, lon_pole=180.0)

rotation2d_inputs = np.array([[1.0, 0.0], [0.0, 1.0], [3.0, 4.0], [-2.0, 1.5]])
rotation2d_outputs = np.array(
    [list(rotation2d(row[0], row[1])) for row in rotation2d_inputs]
)

euler_inputs = np.array([[10.0, 20.0], [30.0, 40.0], [-15.0, 25.0], [45.0, -10.0]])
euler_outputs = np.array(
    [list(euler_angle(row[0], row[1])) for row in euler_inputs]
)

rotate_seq_inputs = np.array(
    [[10.0, 20.0, 30.0], [30.0, 40.0, 50.0], [-15.0, 25.0, 10.0], [45.0, -10.0, 20.0]]
)
rotate_seq_outputs = np.array(
    [list(rotate_seq(row[0], row[1], row[2])) for row in rotate_seq_inputs]
)

n2c_inputs = np.array([[0.0, 0.0], [10.0, 20.0], [-15.0, 30.0], [45.0, -10.0]])
n2c_outputs = np.array(
    [list(native2celestial(row[0], row[1])) for row in n2c_inputs]
)

c2n_inputs = np.array([[0.0, 0.0], [10.0, 20.0], [-15.0, 30.0], [45.0, -10.0]])
c2n_outputs = np.array(
    [list(celestial2native(row[0], row[1])) for row in c2n_inputs]
)

rotation2d_inv = rotation2d.inverse
rotation2d_inv_inputs = rotation2d_outputs.copy()
rotation2d_inv_outputs = np.array(
    [list(rotation2d_inv(row[0], row[1])) for row in rotation2d_inv_inputs]
)

euler_inv = euler_angle.inverse
euler_inv_inputs = euler_outputs.copy()
euler_inv_outputs = np.array(
    [list(euler_inv(row[0], row[1])) for row in euler_inv_inputs]
)

rotate_seq_inv = rotate_seq.inverse
rotate_seq_inv_inputs = rotate_seq_outputs.copy()
rotate_seq_inv_outputs = np.array(
    [list(rotate_seq_inv(row[0], row[1], row[2])) for row in rotate_seq_inv_inputs]
)

n2c_inv = native2celestial.inverse
n2c_inv_inputs = n2c_outputs.copy()
n2c_inv_outputs = np.array(
    [list(n2c_inv(row[0], row[1])) for row in n2c_inv_inputs]
)

c2n_inv = celestial2native.inverse
c2n_inv_inputs = c2n_outputs.copy()
c2n_inv_outputs = np.array(
    [list(c2n_inv(row[0], row[1])) for row in c2n_inv_inputs]
)

af["test_cases"] = [
    {
        "name": "rotation2d",
        "transform": rotation2d,
        "forward_inputs": rotation2d_inputs,
        "forward_outputs": rotation2d_outputs,
        "has_inverse": True,
        "inverse_inputs": rotation2d_inv_inputs,
        "inverse_outputs": rotation2d_inv_outputs,
    },
    {
        "name": "euler_angle_rotation",
        "transform": euler_angle,
        "forward_inputs": euler_inputs,
        "forward_outputs": euler_outputs,
        "has_inverse": True,
        "inverse_inputs": euler_inv_inputs,
        "inverse_outputs": euler_inv_outputs,
    },
    {
        "name": "rotate_sequence_3d",
        "transform": rotate_seq,
        "forward_inputs": rotate_seq_inputs,
        "forward_outputs": rotate_seq_outputs,
        "has_inverse": True,
        "inverse_inputs": rotate_seq_inv_inputs,
        "inverse_outputs": rotate_seq_inv_outputs,
    },
    {
        "name": "rotate_native2celestial",
        "transform": native2celestial,
        "forward_inputs": n2c_inputs,
        "forward_outputs": n2c_outputs,
        "has_inverse": True,
        "inverse_inputs": n2c_inv_inputs,
        "inverse_outputs": n2c_inv_outputs,
    },
    {
        "name": "rotate_celestial2native",
        "transform": celestial2native,
        "forward_inputs": c2n_inputs,
        "forward_outputs": c2n_outputs,
        "has_inverse": True,
        "inverse_inputs": c2n_inv_inputs,
        "inverse_outputs": c2n_inv_outputs,
    },
]
