//
//  Copyright © 2024 Dynatrace LLC. All rights reserved.
//

package com.devlopersquad.jetpackcompose

import com.devlopersquad.jetpackcompose.ui.theme.Piecemeal

@Piecemeal
class Person(
    val name: String,
    val nickname: String? = name,
    val age: Int = 0,
) {
    override fun toString(): String {
        return "Person{name=$name,nickname=$nickname,age=$age}"
    }
}

