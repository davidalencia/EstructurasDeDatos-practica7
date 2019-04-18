package mx.unam.ciencias.edd;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Clase para gráficas. Una gráfica es un conjunto de vértices y aristas, tales
 * que las aristas son un subconjunto del producto cruz de los vértices.
 */
public class Grafica<T> implements Coleccion<T> {

    /* Clase interna privada para iteradores. */
    private class Iterador implements Iterator<T> {

        /* Iterador auxiliar. */
        private Iterator<Vertice> iterador;

        /* Construye un nuevo iterador, auxiliándose de la lista de vértices. */
        public Iterador() {
            iterador =  vertices.iterator();
        }

        /* Nos dice si hay un siguiente elemento. */
        @Override public boolean hasNext() {
            return iterador.hasNext();
        }

        /* Regresa el siguiente elemento. */
        @Override public T next() {
            return iterador.next().elemento;
        }
    }

    /* Clase interna privada para vértices. */
    private class Vertice implements VerticeGrafica<T> {

        /* El elemento del vértice. */
        public T elemento;
        /* El color del vértice. */
        public Color color;
        /* La lista de vecinos del vértice. */
        public Lista<Vertice> vecinos;

        /* Crea un nuevo vértice a partir de un elemento. */
        public Vertice(T elemento) {
          this.elemento = elemento;
	        color = Color.NINGUNO;
	        vecinos = new Lista<Vertice>();
        }

        /* Regresa el elemento del vértice. */
        @Override public T get() {
            return elemento;
        }

        /* Regresa el grado del vértice. */
        @Override public int getGrado() {
            return vecinos.getElementos();
        }

        /* Regresa el color del vértice. */
        @Override public Color getColor() {
            return color;
        }

        /* Regresa un iterable para los vecinos. */
        @Override public Iterable<? extends VerticeGrafica<T>> vecinos() {
            return vecinos;
        }

        private boolean iguales(Vertice v){
          if(!elemento.equals(v.elemento))
            return false;
          if(vecinos.getElementos()!=v.vecinos.getElementos())
            return false;
          for (Vertice u: v.vecinos)
            if(!esVecino(u))
              return false;
          return true;
        }
        private boolean esVecino(Vertice v){
          for (Vertice u: vecinos)
            if(u.elemento.equals(v.elemento))
              return true;
          return false;
        }

        private void enchufa(Vertice v){
          if(v==null)
            return;
          vecinos.agrega(v);
        }

        private void desenchufa(Vertice v){
          if(v==null)
            return;
          if(!vecinos.contiene(v))
            throw new IllegalArgumentException();
          vecinos.elimina(v);
        }
    }

    /* Vértices. */
    private Lista<Vertice> vertices;
    /* Número de aristas. */
    private int aristas;

    /**
     * Constructor único.
     */
    public Grafica() {
        vertices = new Lista<Vertice>();
    }

    /**
     * Regresa el número de elementos en la gráfica. El número de elementos es
     * igual al número de vértices.
     * @return el número de elementos en la gráfica.
     */
    @Override public int getElementos() {
        return vertices.getElementos();
    }

    /**
     * Regresa el número de aris6tas.
     * @return el número de aristas.
     */
    public int getAristas() {
        return aristas;
    }

    /**
     * Agrega un nuevo elemento a la gráfica.
     * @param elemento el elemento a agregar.
     * @throws IllegalArgumentException si el elemento ya había sido agregado a
     *         la gráfica.
     */
    @Override public void agrega(T elemento) {
      if(elemento==null)
        throw new IllegalArgumentException();
      Vertice v = new Vertice(elemento);
      if(busca(elemento)!=null)
        throw new IllegalArgumentException();
      vertices.agrega(v);
    }

    /**
     * Conecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica. El peso de la arista que conecte a los elementos será 1.
     * @param a el primer elemento a conectar.
     * @param b el segundo elemento a conectar.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b ya están conectados, o si a es
     *         igual a b.
     */
    public void conecta(T a, T b) {
        Vertice va = busca(a);
        Vertice vb = busca(b);
        if(va!=null && (a.equals(b) || va.vecinos.contiene(vb)))
         throw new IllegalArgumentException();
        if(va==null || vb==null)
          throw new NoSuchElementException();
        va.enchufa(vb);
        vb.enchufa(va);
        aristas++;
    }

    /**
     * Desconecta dos elementos de la gráfica. Los elementos deben estar en la
     * gráfica y estar conectados entre ellos.
     * @param a el primer elemento a desconectar.
     * @param b el segundo elemento a desconectar.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     * @throws IllegalArgumentException si a o b no están conectados.
     */
    public void desconecta(T a, T b) {
      Vertice va = busca(a);
      Vertice vb = busca(b);
      if(va==null || vb==null)
        throw new NoSuchElementException();
      va.desenchufa(vb);
      vb.desenchufa(va);
      aristas--;
    }

    /**
     * Nos dice si el elemento está contenido en la gráfica.
     * @return <tt>true</tt> si el elemento está contenido en la gráfica,
     *         <tt>false</tt> en otro caso.
     */
    @Override public boolean contiene(T elemento) {
        return busca(elemento)!=null;
    }

    /**
     * Elimina un elemento de la gráfica. El elemento tiene que estar contenido
     * en la gráfica.
     * @param elemento el elemento a eliminar.
     * @throws NoSuchElementException si el elemento no está contenido en la
     *         gráfica.
     */
    @Override public void elimina(T elemento) {
        Vertice v = busca(elemento);
        if(v==null)
          throw new NoSuchElementException();
        for(Vertice u: v.vecinos){
          u.desenchufa(v);
          v.desenchufa(u);
          aristas--;
        }
        vertices.elimina(v);
    }

    /**
     * Nos dice si dos elementos de la gráfica están conectados. Los elementos
     * deben estar en la gráfica.
     * @param a el primer elemento.
     * @param b el segundo elemento.
     * @return <tt>true</tt> si a y b son vecinos, <tt>false</tt> en otro caso.
     * @throws NoSuchElementException si a o b no son elementos de la gráfica.
     */
    public boolean sonVecinos(T a, T b) {
        Vertice va = busca(a);
        Vertice vb = busca(b);
        if(va==null || vb==null)
          throw new NoSuchElementException();
        return va.vecinos.contiene(vb);
    }

    /**
     * Regresa el vértice correspondiente el elemento recibido.
     * @param elemento el elemento del que queremos el vértice.
     * @throws NoSuchElementException si elemento no es elemento de la gráfica.
     * @return el vértice correspondiente el elemento recibido.
     */
    public VerticeGrafica<T> vertice(T elemento) {
      if(elemento==null)
        return null;
      for(Vertice u: vertices)
        if(elemento.equals(u.elemento))
          return u;
      throw new NoSuchElementException();
    }

    /**
     * Define el color del vértice recibido.
     * @param vertice el vértice al que queremos definirle el color.
     * @param color el nuevo color del vértice.
     * @throws IllegalArgumentException si el vértice no es válido.
     */
    public void setColor(VerticeGrafica<T> vertice, Color color) {
        if (vertice == null || vertice.getClass() != Vertice.class)
            throw new IllegalArgumentException("Vértice inválido");
        Vertice v = (Vertice)vertice;
        v.color = color;
    }

    /**
     * Nos dice si la gráfica es conexa.
     * @return <code>true</code> si la gráfica es conexa, <code>false</code> en
     *         otro caso.
     */
    public boolean esConexa() {
      if(vertices.getElementos()<2)
        return true;
      int[] i= {0};
      Vertice v = vertices.getPrimero();
      recorreYLimpia(v.elemento, (l)->i[0]++, new Cola<Vertice>(), Color.NEGRO);
	    return i[0]==vertices.getElementos();
    }

    /**
     * Realiza la acción recibida en cada uno de los vértices de la gráfica, en
     * el orden en que fueron agregados.
     * @param accion la acción a realizar.
     */
    public void paraCadaVertice(AccionVerticeGrafica<T> accion) {
        for (Vertice v: vertices)
          accion.actua(v);
    }

    /**
     * Realiza la acción recibida en todos los vértices de la gráfica, en el
     * orden determinado por BFS, comenzando por el vértice correspondiente al
     * elemento recibido. Al terminar el método, todos los vértices tendrán
     * color {@link Color#NINGUNO}.
     * @param elemento el elemento sobre cuyo vértice queremos comenzar el
     *        recorrido.
     * @param accion la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la gráfica.
     */
    public void bfs(T elemento, AccionVerticeGrafica<T> accion) {
      recorreYLimpia(elemento, accion, new Cola<Vertice>(), Color.NEGRO);
    }

    /**
     * Realiza la acción recibida en todos los vértices de la gráfica, en el
     * orden determinado por DFS, comenzando por el vértice correspondiente al
     * elemento recibido. Al terminar el método, todos los vértices tendrán
     * color {@link Color#NINGUNO}.
     * @param elemento el elemento sobre cuyo vértice queremos comenzar el
     *        recorrido.
     * @param accion la acción a realizar.
     * @throws NoSuchElementException si el elemento no está en la gráfica.
     */
    public void dfs(T elemento, AccionVerticeGrafica<T> accion) {
      recorreYLimpia(elemento, accion, new Pila<Vertice>(), Color.NEGRO);
    }

    /**
     * Nos dice si la gráfica es vacía.
     * @return <code>true</code> si la gráfica es vacía, <code>false</code> en
     *         otro caso.
     */
    @Override public boolean esVacia() {
	       return vertices.esVacia();
    }

    /**
     * Limpia la gráfica de vértices y aristas, dejándola vacía.
     */
    @Override public void limpia() {
        vertices = new Lista<Vertice>();
        aristas = 0;
    }

    /**
     * Regresa una representación en cadena de la gráfica.
     * @return una representación en cadena de la gráfica.
     */
    @Override public String toString() {
      String s[] = {"{"};
      for (Vertice v: vertices)
        s[0] += v.elemento+", ";
      s[0] += "}, {";
      for (Vertice v: vertices){
        Vertice w = (Vertice) v;
        w.color = Color.NEGRO;
        for (Vertice u: w.vecinos)
          if(!u.color.equals(Color.NEGRO))
            s[0] += String.format("(%d, %d), ", w.elemento, u.elemento);
      }
      s[0] += "}";
      return s[0];
    }

    /**
     * Nos dice si la gráfica es igual al objeto recibido.
     * @param objeto el objeto con el que hay que comparar.
     * @return <tt>true</tt> si la gráfica es igual al objeto recibido;
     *         <tt>false</tt> en otro caso.
     */
    @Override public boolean equals(Object objeto) {
        if (objeto == null || getClass() != objeto.getClass())
            return false;
        @SuppressWarnings("unchecked") Grafica<T> grafica = (Grafica<T>)objeto;
        if(vertices.getElementos()!=grafica.vertices.getElementos())
          return false;
        for (Vertice v: grafica.vertices)
          if(!contiene(v))
            return false;
	      return true;
    }
    private boolean contiene(Vertice v){
      for (Vertice u: vertices)
        if(u.iguales(v))
          return true;
      return false;
    }

    /**
     * Regresa un iterador para iterar la gráfica. La gráfica se itera en el
     * orden en que fueron agregados sus elementos.
     * @return un iterador para iterar la gráfica.
     */
    @Override public Iterator<T> iterator() {
        return new Iterador();
    }

    private Vertice busca(T e){
      try{
        return (Vertice) vertice(e);
      }catch (NoSuchElementException err) {
        return null;
      }
    }

    private void recorrido(T e,
                           AccionVerticeGrafica<T> accion,
                           MeteSaca<Vertice> ms,
                           Color c){
      Vertice v =(Vertice) vertice(e);
      v.color = c;
      ms.mete(v);
      while(!ms.esVacia()){
        v = ms.saca();
        accion.actua(v);
        for (Vertice u: v.vecinos)
          if(!u.color.equals(c)){
            u.color = c;
            ms.mete(u);
          }
      }
    }

    private void recorreYLimpia(T e,
                           AccionVerticeGrafica<T> accion,
                           MeteSaca<Vertice> ms,
                           Color c){
       recorrido(e, accion, ms, c);
       recorrido(e, (vl)->{}, ms, Color.NINGUNO);
    }
}
