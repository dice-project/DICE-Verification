FROM ubuntu:14.04
MAINTAINER Francesco Marconi <francesco.marconi@polimi.it>

# Setting the environment
ENV USERHOME  /root
ENV DEBIAN_FRONTEND noninteractive
ENV USER root

WORKDIR /tmp
# Update the repos and install all the used packages
RUN apt-get update && apt-get install -y --force-yes --no-install-recommends \
    unzip \
    xorg \
    gcc \
    g++ \
    make \
    git \
    python python-dev python-distribute python-pip python-tk \
    python-matplotlib \
    sbcl \
    zlib1g-dev \
    curl && \
    apt-get autoclean && \
    apt-get autoremove && \
    rm -rf /var/lib/apt/lists/*

#installing minisat
    RUN curl -L http://minisat.se/downloads/minisat-2.2.0.tar.gz -o /tmp/minisat-2.2.0.tar.gz
    WORKDIR /tmp
    RUN tar -xvzf minisat-2.2.0.tar.gz && \
        export MROOT=/tmp/minisat &&\
        cd minisat/core/ && \
        make && \
        cp minisat /usr/local/bin/minisat

# installing z3
    RUN curl -L https://github.com/Z3Prover/z3/releases/download/z3-4.4.1/z3-4.4.1-x64-ubuntu-14.04.zip -o /tmp/z3.zip && \
	unzip z3.zip -d /tmp && \
	cp /tmp/z3-4.4.1-x64-ubuntu-14.04/bin/z3 /usr/local/bin/z3

# installing zot
    RUN git clone https://github.com/fm-polimi/zot /usr/local/zot/ && \
        ln -s /usr/local/zot/asdfs/*.asd /usr/lib/sbcl/site-systems/ && \
        cp /usr/local/zot/bin/zot /usr/local/bin/
#get D-VerT from github
    RUN git clone https://github.com/dice-project/DICE-Verification /opt/DICE-Verification
#load all needed packages from pip
    RUN pip install -r /opt/DICE-Verification/installation/requirements.txt
#apply modifications to zot
    WORKDIR /opt/DICE-Verification
    RUN cp zot_changes/*.lisp /usr/local/zot/asdfs
#run verification
    WORKDIR json2mc
    ENTRYPOINT ["./json2mc.py"]
#    CMD ["-h"]


#    curl -L https://bootstrap.pypa.io/get-pip.py -o /opt/get-pip
#    python /opt/get-pip


