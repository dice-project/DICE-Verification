# D-VerT - json2mc module
Docker container for the json2mc module of D-VerT.

## How to Build it
```sh
docker build [—no-cache=true] -t json2mc . 
```

## How to run it
``` sh
docker run -ti —-name <container-name> \
                -v  <local path to output-files>:/opt/DICE-Verification/json2mc/output-dir \
                --entrypoint /bin/bash json2mc \
                -c ./json2mc.py -o /opt/DICE-Verification/json2mc/output-dir
```
