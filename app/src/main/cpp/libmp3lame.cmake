cmake_minimum_required(VERSION 3.31.6)

# LIBMP3LAME FETCH SECTION: START

set(LIBMP3LAME_URL https://downloads.videolan.org/pub/contrib/lame/lame-3.100.tar.gz)

set(LIBMP3LAME_NAME "lame-3")

IF (NOT EXISTS ${CMAKE_CURRENT_SOURCE_DIR}/${LIBMP3LAME_NAME})
    file(DOWNLOAD ${LIBMP3LAME_URL} ${CMAKE_CURRENT_SOURCE_DIR}/lame-3.tar.gz)

    execute_process(
            COMMAND ${CMAKE_COMMAND} -E tar xzf lame-3.tar.gz
            WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
    )

    file(RENAME lame-3.100 lame-3)

    # We're patching install step manually because it installs libx264 with version suffix and Android won't have it
    file(READ ${CMAKE_CURRENT_SOURCE_DIR}/${LIBMP3LAME_NAME}/configure configure_src)
    string(REPLACE "echo \"SONAME=libmp3lame.so.$API\" >> config.mak" "echo \"SONAME=libmp3lame.so\" >> config.mak" configure_src "${configure_src}")
    file(WRITE ${CMAKE_CURRENT_SOURCE_DIR}/${LIBMP3LAME_NAME}/configure "${configure_src}")

    file(READ ${CMAKE_CURRENT_SOURCE_DIR}/${LIBMP3LAME_NAME}/Makefile.am makefile_src)
    file(WRITE ${CMAKE_CURRENT_SOURCE_DIR}/${LIBMP3LAME_NAME}/Makefile.am "${makefile_src}")
ENDIF()

file(
        COPY ${CMAKE_CURRENT_SOURCE_DIR}/libmp3lame_build_system.cmake
        DESTINATION ${CMAKE_CURRENT_SOURCE_DIR}/${LIBMP3LAME_NAME}
        FILE_PERMISSIONS OWNER_READ OWNER_WRITE OWNER_EXECUTE GROUP_READ GROUP_EXECUTE WORLD_READ WORLD_EXECUTE
)

# LIBMP3LAME FETCH SECTION: END

# ANDROID BUILD TOOLS SECTION: START

# Setting custom variables for build tools
set(LIBMP3LAME_CC ${CMAKE_C_COMPILER})
set(LIBMP3LAME_AR ${ANDROID_AR})
set(LIBMP3LAME_AS ${ANDROID_ASM_COMPILER})

# Toolchain settings taken from Android NDK documentation
set(LIBMP3LAME_RANLIB ${ANDROID_TOOLCHAIN_PREFIX}ranlib${ANDROID_TOOLCHAIN_SUFFIX})
set(LIBMP3LAME_STRIP ${ANDROID_TOOLCHAIN_ROOT}/bin/llvm-strip${ANDROID_TOOLCHAIN_SUFFIX})

# ANDROID BUILD TOOLS SECTION: END

# ANDROID FLAGS SECTION: START

# Removing fatal warnings flag due to issues with newer versions of Android NDK
string(REPLACE " -Wl,--fatal-warnings" "" LIBMP3LAME_LD_FLAGS ${CMAKE_SHARED_LINKER_FLAGS})

string(STRIP ${CMAKE_C_FLAGS} LIBMP3LAME_C_FLAGS)
string(STRIP ${LIBMP3LAME_LD_FLAGS} LIBMP3LAME_LD_FLAGS)

set(LIBMP3LAME_C_FLAGS "${LIBMP3LAME_C_FLAGS} --target=${ANDROID_LLVM_TRIPLE} --gcc-toolchain=${ANDROID_TOOLCHAIN_ROOT}")
set(LIBMP3LAME_ASM_FLAGS "${CMAKE_ASM_FLAGS} --target=${ANDROID_LLVM_TRIPLE}")
set(LIBMP3LAME_LD_FLAGS "${LIBMP3LAME_C_FLAGS} ${LIBMP3LAME_LD_FLAGS}")

# ANDROID FLAGS SECTION: END

# MISC VARIABLES SECTION: START

set(NJOBS 4)
set(HOST_BIN ${ANDROID_NDK}/prebuilt/${ANDROID_HOST_TAG}/bin)

# MISC VARIABLES SECTION: END

# CONFIGURATION FLAGS SECTION: START

set(LIBMP3LAME_CONFIGURE_EXTRAS "--enable-nasm") # Enabling NASM for better performance on ARM platforms

# Adding CPU-specific optimizations based on ABI
IF (${CMAKE_ANDROID_ARCH_ABI} MATCHES "^armv7|aarch64")
    list(APPEND LIBMP3LAME_CONFIGURE_EXTRAS "--cpu=cortex-a9") # Cortex-A9 is used widely across ARM devices
ELSEIF (${CMAKE_ANDROID_ARCH_ABI} MATCHES "^x86|^i686")
    list(APPEND LIBMP3LAME_CONFIGURE_EXTRAS "--cpu=i686") # i686 for older Intel CPUs
ENDIF()

string(REPLACE ";" "|" LIBMP3LAME_CONFIGURE_EXTRAS_ENCODED "${LIBMP3LAME_CONFIGURE_EXTRAS}")

# CONFIGURATION FLAGS SECTION: END

# LIBMP3LAME EXTERNAL PROJECT CONFIG SECTION: START
ExternalProject_Add(libmp3lame_target
        PREFIX libmp3lame_pref
        URL ${CMAKE_CURRENT_SOURCE_DIR}/${LIBMP3LAME_NAME}
        DOWNLOAD_NO_EXTRACT 1
        CONFIGURE_COMMAND ${CMAKE_COMMAND} -E env
        CFLAGS=-mtune
        CC=${LIBMP3LAME_CC}
        AS=${LIBMP3LAME_AS}
        AR=${LIBMP3LAME_AR}
        RANLIB=${LIBMP3LAME_RANLIB}
        STRIP=${LIBMP3LAME_STRIP}
        ${CMAKE_COMMAND}
        -DSTEP:STRING=configure
        -DHOST:STRING=${ANDROID_LLVM_TRIPLE}
        -DSYSROOT:STRING=${CMAKE_SYSROOT}
        -DC_FLAGS:STRING=${LIBMP3LAME_C_FLAGS}
        -DAS_FLAGS:STRING=${LIBMP3LAME_ASM_FLAGS}
        -DLD_FLAGS:STRING=${LIBMP3LAME_LD_FLAGS}
        -DPREFIX:STRING=${CMAKE_LIBRARY_OUTPUT_DIRECTORY}
        -DCONFIGURE_EXTRAS:STRING=${LIBMP3LAME_CONFIGURE_EXTRAS_ENCODED}
        -P libmp3lame_build_system.cmake
        BUILD_COMMAND ${CMAKE_COMMAND}
        -DSTEP:STRING=build
        -DNJOBS:STRING=${NJOBS}
        -DHOST_TOOLCHAIN:STRING=${HOST_BIN}
        -P libmp3lame_build_system.cmake
        BUILD_IN_SOURCE 1
        INSTALL_COMMAND ${CMAKE_COMMAND}
        -DSTEP:STRING=install
        -DHOST_TOOLCHAIN:STRING=${HOST_BIN}
        -P libmp3lame_build_system.cmake
        LOG_BUILD 1
        LOG_INSTALL 1
        LOG_CONFIGURE 1
)

# LIBMP3LAME EXTERNAL PROJECT CONFIG SECTION: END