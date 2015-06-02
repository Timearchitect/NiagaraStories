package se.mah.k3;

public interface RenderOrder {
void toBottom(Object o);
void toTop(Object o);
void toIndex(int i);
}
