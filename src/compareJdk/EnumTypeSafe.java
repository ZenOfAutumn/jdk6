package compareJdk;

/**
 * Created by liuda on 2016/10/4.
 */
public enum EnumTypeSafe {
    SUMMER(55,"summer"),
    WINTER(60,"winter"),
    AUTUM(70,"autun");
    private int degree;
    private String season;
   EnumTypeSafe(int degree,String season){
        this.degree = degree;
        this.season = season;
    }
    public int degree(){return degree;}
    public String season(){return season;}
}
