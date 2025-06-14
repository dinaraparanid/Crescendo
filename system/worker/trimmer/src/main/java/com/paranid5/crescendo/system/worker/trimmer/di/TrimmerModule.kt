package com.paranid5.crescendo.system.worker.trimmer.di

import com.paranid5.crescendo.system.worker.trimmer.TrimAudioFileUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val trimmerWorkerModule = module {
    singleOf(::TrimAudioFileUseCase)
}
