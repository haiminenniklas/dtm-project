package me.tr.dtm.main.other.callback;

@FunctionalInterface
public interface TypedCallback<T> {

    public void execute(T type);

}
