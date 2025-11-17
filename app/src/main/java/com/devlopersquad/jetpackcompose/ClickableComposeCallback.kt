//
//  Copyright © 2025 Dynatrace LLC. All rights reserved.
//

package com.devlopersquad.jetpackcompose

//has to be moved to instrumentation module
public class ClickableComposeCallback(private val function: Function0<Unit>) : Function0<Unit> {
    override fun invoke() {
        println("dsadsa")
        function.invoke()
    }
}
