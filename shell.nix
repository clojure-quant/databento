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

  oldPkgs = import (builtins.fetchTarball {
   url = "https://github.com/NixOS/nixpkgs/archive/44c64ea5d7d1ce931e616313de11ac171dbd6a40.tar.gz";
  }) { system = pkgs.system; };

in
pkgs.mkShell {
  packages = [ pyEnv
               pkgs.nodejs_24   # ⭐ Node.js for gemini-cli
               pkgs.gemini-cli
               oldPkgs.duckdb.lib
           ];

  shellHook = ''
    echo "✅ Pure nix Databento environment ready"
    export NPM_PREFIX=$PWD/.npm-global
    export PATH=$NPM_PREFIX/bin:$PATH

    mkdir -p $NPM_PREFIX

    if ! command -v gemini >/dev/null; then
      echo "Installing @google/gemini-cli locally..."
      npm install @google/gemini-cli
      ln -sf $PWD/node_modules/.bin/gemini $NPM_PREFIX/bin/gemini
    fi

    export DUCKDB_LIB_DIR=${oldPkgs.duckdb.lib}/lib
    export LD_LIBRARY_PATH=${oldPkgs.duckdb.lib}/lib:$LD_LIBRARY_PATH
    echo "DuckDB lib at $DUCKDB_LIB_DIR"
    echo "DuckDB shared lib: $DUCKDB_LIB_DIR/libduckdb.so"

  '';
}
