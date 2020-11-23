class Merchandise {
    merchID = -1
    merchName = ""
    price = 0
    category = ""
    description = ""
    quantity = 0

    constructor(merchId, merchName, price, category, description, quantity) {
        this.merchID = merchId;
        this.merchName = merchName;
        this.price = price;
        this.category = category;
        this.description = description;
        this.quantity = quantity;
    }

    get imageFileName() {
        return `${(this.merchID).pad(2)}.png`;
    }

    get prettyPrice() {
        return `$${(this.price).toFixed(2)}`;
    }

    static FromJsonObject(jsonObj) {
        return new Merchandise(jsonObj.merchID, jsonObj.merchName, jsonObj.price, jsonObj.category, jsonObj.description, jsonObj.quantity);
    }
}

class CartItem {
    merchID = -1;
    quantity = 0;

    constructor(merchId, quantity) {
        this.merchID = merchId;
        this.quantity = quantity;
    }

    static FromJsonObj(jsonObj) {
        return new CartItem(jsonObj.merchID, jsonObj.quantity);
    }
}