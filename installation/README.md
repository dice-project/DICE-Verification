# D-VerT - json2mc module
Docker file to build a Docker image of the json2mc module of D-VerT.

## How to Build it
```sh
docker build -t json2mc .
```

## How to run it
The following command will launch a container running a verification task on the first topology presented in [deliverable 3.5](http://wp.doc.ic.ac.uk/dice-h2020/wp-content/uploads/sites/75/2016/02/D3.5_DICE-verification-tools-Initial-version.pdf). Change `deliverable_1` with `deliverable_2` to run it on the second topology in the deliverable.
``` sh
docker run -ti —-name <container-name> \
                -v  <local path to output-files>:/opt/DICE-Verification/json2mc/output-dir \
                --entrypoint /bin/bash json2mc \
                -c ./json2mc.py -m deliverable_1
```
