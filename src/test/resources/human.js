var __extends = this.__extends || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
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
