cmake_minimum_required(VERSION 3.31.6)

if (${STEP} STREQUAL configure)
    # Decode the extra arguments passed in
    string(REPLACE "|" ";" CONFIGURE_EXTRAS_ENCODED "${CONFIGURE_EXTRAS}")
    list(REMOVE_ITEM CONFIGURE_EXTRAS_ENCODED "")

    # Construct the final configure command
    set(CONFIGURE_COMMAND
            ./configure
            --enable-shared
            --extra-cflags="${C_FLAGS}"
            --extra-ldflags="${LD_FLAGS}"
            --extra-asflags="${AS_FLAGS}"
            --sysroot=${SYSROOT}
            --host=${HOST}
            --enable-pic
            --libdir=${PREFIX}
            --prefix=${PREFIX}
            ${CONFIGURE_EXTRAS_ENCODED}
    )

    # Execute the configure phase
    execute_process(COMMAND ${CONFIGURE_COMMAND})
elseif(${STEP} STREQUAL build)
    # Build the compiled objects using make
    execute_process(COMMAND ${HOST_TOOLCHAIN}/make -j${NJOBS})
elseif(${STEP} STREQUAL install)
    # Install the generated binaries and headers
    execute_process(COMMAND ${HOST_TOOLCHAIN}/make install)
endif()