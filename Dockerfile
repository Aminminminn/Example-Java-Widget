FROM eclipse-temurin:11

USER root

RUN apt-get update && \
    apt-get install -y \
        make \
        python3 \
        python3-pip \
        wget \
        ninja-build \
        xvfb

RUN pip3 install west

# Install cmake
RUN wget https://cmake.org/files/v3.30/cmake-3.30.3-linux-x86_64.tar.gz
RUN tar zxvf cmake-3.30.3-linux-x86_64.tar.gz --directory=/opt
RUN ln -sf /opt/cmake-3.30.3-linux-x86_64/bin/* /usr/bin/


# Get Arm toolchain
RUN wget https://developer.arm.com/-/media/Files/downloads/gnu/13.2.rel1/binrel/arm-gnu-toolchain-13.2.rel1-x86_64-arm-none-eabi.tar.xz

# Install Arm toolchain
RUN tar -xf arm-gnu-toolchain-13.2.rel1-x86_64-arm-none-eabi.tar.xz --directory=/opt

# Add ARGMCC_DIR environment variable
ENV ARMGCC_DIR /opt/arm-gnu-toolchain-13.2.Rel1-x86_64-arm-none-eabi/

RUN mkdir -p /home/build/workspace
RUN chown 1000 /home/build/workspace

USER 1000

ENV ACCEPT_MICROEJ_SDK_EULA_V3_1C=YES ACCEPT_MICROEJ_SDK_EULA_V3_1B=YES

WORKDIR /home/build/workspace
COPY . /home/build/workspace

RUN mkdir ~/.microej && mv licenses ~/.microej
CMD ./gradlew buildExecutable