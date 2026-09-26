package com.example.rccarcontroller.Domain.Model

sealed class Command {
    object Forward : Command()
    object Backward : Command()
    object Left : Command()
    object Right : Command()
    object Stop : Command()
    data class Speed(val value: Int) : Command()
}
