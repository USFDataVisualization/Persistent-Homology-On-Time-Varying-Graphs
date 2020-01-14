#!/bin/bash

. venv/bin/activate

export FLASK_APP=main.py
export FLASK_RUN_PORT=6000

flask run --host 0.0.0.0

