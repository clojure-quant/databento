{ lib
, buildPythonPackage
, fetchurl
}:

buildPythonPackage rec {
  pname = "databento-dbn";
  version = "0.46.0";

  format = "wheel";

  src = fetchurl {
    url = "https://files.pythonhosted.org/packages/90/bc/653c2e72eb6f72c85729f147c8fb0099d03aba3399a0e78c22d9d184e761/databento_dbn-0.46.0-cp312-cp312-manylinux_2_24_x86_64.whl";
    hash = "sha256-98fjgh8qIIbvL/MmEevjc28kqOirHxewXUSiumWxoA0=";


  };

  # wheels already built
  doCheck = false; 

  pythonImportsCheck = [ "databento_dbn" ];

  meta = {
    description = "Databento DBN decoder";
    license = lib.licenses.asl20;
  };
}
