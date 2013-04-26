var __extends = this.__extends || function (d, b) {
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
var Human = (function (_super) {
    __extends(Human, _super);
    function Human() {
        _super.call(this);
    }
    return Human;
})(Mammal);
var __extends = this.__extends || function (d, b) {
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
var Mammal = (function (_super) {
    __extends(Mammal, _super);
    function Mammal() {
        _super.call(this);
    }
    return Mammal;
})(Animal);
var Animal = (function () {
    function Animal() {
    }
    Animal.prototype.move = function () {
    };
    return Animal;
})();
