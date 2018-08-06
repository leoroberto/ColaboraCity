package br.com.lfsolucoes.colaboracity.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class details{

    var ObjectId : String? = null
    var tituloProblema : String? = null
    var pontoReferencia: String? = null
    var imagem : String? = null

    constructor() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    constructor(ObjectId : String, tituloProblema : String, pontoReferencia: String, imagem : String) {
        this.ObjectId = ObjectId
        this.imagem = imagem
        this.pontoReferencia = pontoReferencia
        this.tituloProblema = tituloProblema
    }
}