//
//  Copyright © 2024 Dynatrace LLC. All rights reserved.
//

package com.devlopersquad.compiler.piecemeal

import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class PiecemealFirExtensionRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::PiecemealFirStatusTransformerExtension
        +::PiecemealFirGenerationExtension
    }
}
