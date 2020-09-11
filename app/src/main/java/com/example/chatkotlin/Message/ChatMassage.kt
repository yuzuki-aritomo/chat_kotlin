package com.example.chatkotlin

class  ChatMassage(val id: String, val text: String, val toId: String, val fromId: String, val timestamp: Long ){
    constructor() : this("","","","",-1)
}