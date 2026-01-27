{ pkgs ? import <nixpkgs> {} }:

let
  python = pkgs.python312;

  databento-dbn = python.pkgs.callPackage ./databento-dbn.nix {};



  # ---------------------------
  # databento
  # ---------------------------
  databento = python.pkgs.buildPythonPackage rec {
    pname = "databento";
    version = "0.69.0";
    format = "pyproject";

    src = python.pkgs.fetchPypi {
      inherit pname version;
      hash = "sha256-ySg5o6uZbHBaRSePpFdcygE5HLU30TIPWe83QbNcqQE=";
    };

    nativeBuildInputs = with python.pkgs; [
      poetry-core
    ];

    propagatedBuildInputs = with python.pkgs; [
      aiohttp
      pyarrow
      numpy
      pandas
      requests
      websockets
      zstandard
      databento-dbn   # ⭐ now provided
    ];

    doCheck = false;
  };


  pyEnv = python.withPackages (ps: [
    ps.ipython
    databento
  ]);

in
pkgs.mkShell {
  packages = [ pyEnv ];

  shellHook = ''
    echo "✅ Pure nix Databento environment ready"
  '';
}
