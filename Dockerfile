FROM eclipse-temurin:11

USER root

RUN apt-get update && \
    apt-get install -y \
        make \
        wget \
        ninja-build \
        xvfb \
        xz-utils 

# Install cmake
RUN wget https://cmake.org/files/v3.30/cmake-3.30.3-linux-x86_64.tar.gz
RUN tar zxvf cmake-3.30.3-linux-x86_64.tar.gz --directory=/opt
RUN ln -sf /opt/cmake-3.30.3-linux-x86_64/bin/* /usr/bin/


# Get Arm toolchain
RUN wget https://developer.arm.com/-/media/Files/downloads/gnu/13.2.rel1/binrel/arm-gnu-toolchain-13.2.rel1-x86_64-arm-none-eabi.tar.xz

# Install Arm toolchain
RUN tar -xf arm-gnu-toolchain-13.2.rel1-x86_64-arm-none-eabi.tar.xz --directory=/opt

# Add ARGMCC_DIR environment variable
ENV ARMGCC_DIR=/opt/arm-gnu-toolchain-13.2.Rel1-x86_64-arm-none-eabi/


# Build the project application
ENV ACCEPT_MICROEJ_SDK_EULA_V3_1C=YES ACCEPT_MICROEJ_SDK_EULA_V3_1B=YES

WORKDIR /home/build/workspace
COPY . /home/build/workspace

RUN mv .microej/ ~/

CMD ./gradlew buildExecutable --info