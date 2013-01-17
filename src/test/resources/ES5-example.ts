/**
 * This class uses ECMA5 style getters,
 * so requires the compiler property:
 * --target ES5
 */
class Greeter {
	_greeting: string;
	get greeting():String {
		return this._greeting;
	}
}