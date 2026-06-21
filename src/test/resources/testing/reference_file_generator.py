import sys
from io import BytesIO

import asdf
import numpy as np
from astropy import coordinates as coord
from astropy.modeling import models

import gwcs.coordinate_frames as cf
import gwcs.wcs as gwcs_wcs


def main():
    af = asdf.AsdfFile()

    script = sys.stdin.read()
    env = {
        "af": af,
        "np": np,
        "models": models,
        "cf": cf,
        "gwcs_wcs": gwcs_wcs,
        "coord": coord,
    }
    exec(script, env)

    buffer = BytesIO()
    af.write_to(buffer)

    buffer.seek(0)
    sys.stdout.buffer.write(buffer.read())


if __name__ == "__main__":
    main()
