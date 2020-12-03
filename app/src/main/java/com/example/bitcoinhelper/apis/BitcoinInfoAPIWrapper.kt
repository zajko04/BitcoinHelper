package com.example.bitcoinhelper.apis

class BitcoinInfoAPIWrapper {
    companion object {

        private var api: Any? = null
        private var type: APIType? = null

        fun getAllAPIs(): ArrayList<String> {
            return arrayListOf(APIType.COINBASE.value, APIType.BITBAY.value)
        }

        fun initialize(type: APIType) {
            this.type = type
            api = when(type) {
                APIType.COINBASE -> CoinbaseAPI.getInstance()
                APIType.BITBAY   -> BitBayAPI.getInstance()
            }
        }

        fun getInstance() : API {
            if (api == null && type != null) {
                initialize(type!!)
            }
            return api as API
        }

        fun stop() {
            when (type) {
                APIType.COINBASE -> CoinbaseAPI.stop()
                APIType.BITBAY   -> BitBayAPI.stop()
            }
            api = null
        }
    }

    enum class APIType(
        var value: String
    ) {
        COINBASE("Coinbase"),
        BITBAY("BitBay")
    }
}