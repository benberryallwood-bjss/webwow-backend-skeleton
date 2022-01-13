package webwow.adapters.web;


/**
 * This is a simple data structure that maps directly to the JSON used in the response
 */
class ThingModel {
    String name ;
    int number ;

    public ThingModel(String name, int number) {
        this.name = name;
        this.number = number;
    }
}
