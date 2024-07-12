package com.ruchitech.cashentery.helper

sealed class Result {
    data object Success : Result()
    data object Error : Result()
    data object ResetState : Result()

}