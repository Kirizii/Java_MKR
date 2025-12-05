/*
 * Copyright (c) 2014, NTUU KPI, Computer systems department and/or its affiliates. All rights reserved.
 * NTUU KPI PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 */

package ua.kpi.comsys.test2.implementation;

import ua.kpi.comsys.test2.NumberList;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

/**
 * Custom implementation of INumberList interface.
 * Has to be implemented by each student independently.
 *
 * @author Inviia Kostiantyn Markovich IM-32 №11
 *
 */
public class NumberListImpl implements NumberList {

    private static class Node {
        byte value;
        Node next;
        Node prev;

        Node(byte value) {
            this.value = value;
        }
    }

    private static final int[] BASES = {2, 3, 8, 10, 16};

    private Node head;
    private Node tail;
    private int size;
    private final int base;

    /**
     * Default constructor. Returns empty <tt>NumberListImpl</tt>
     */
    public NumberListImpl() {
        this.base = getMainBase();
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    private NumberListImpl(int base, BigInteger value) {
        this.base = base;
        this.head = null;
        this.tail = null;
        this.size = 0;
        initFromBigInteger(value);
    }

    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * from file, defined in string format.
     *
     * @param file - file where number is stored.
     */
    public NumberListImpl(File file) {
        this();
        loadFromFile(file);
    }


    /**
     * Constructs new <tt>NumberListImpl</tt> by <b>decimal</b> number
     * in string notation.
     *
     * @param value - number in string notation.
     */
    public NumberListImpl(String value) {
        this();
        initFromDecimalString(value);
    }


    /**
     * Saves the number, stored in the list, into specified file
     * in <b>decimal</b> scale of notation.
     *
     * @param file - file where number has to be stored.
     */
    public void saveList(File file) {
        if (file == null) {
            return;
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(toDecimalString());
            bw.newLine();
        } catch (IOException ignored) {
        }
    }


    /**
     * Returns student's record book number, which has 4 decimal digits.
     *
     * @return student's record book number.
     */
    public static int getRecordBookNumber() {
        return 11;
    }

    private static int getMainBase() {
        int c5 = Math.floorMod(getRecordBookNumber(), 5);
        return BASES[c5];
    }

    private static int getAdditionalBase() {
        int c5 = Math.floorMod(getRecordBookNumber(), 5);
        return BASES[(c5 + 1) % BASES.length];
    }


    /**
     * Returns new <tt>NumberListImpl</tt> which represents the same number
     * in other scale of notation, defined by personal test assignment.<p>
     *
     * Does not impact the original list.
     *
     * @return <tt>NumberListImpl</tt> in other scale of notation.
     */
    public NumberListImpl changeScale() {
        BigInteger value = toBigInteger();
        int newBase = getAdditionalBase();
        return new NumberListImpl(newBase, value);
    }


    /**
     * Returns new <tt>NumberListImpl</tt> which represents the result of
     * additional operation, defined by personal test assignment.<p>
     *
     * Does not impact the original list.
     *
     * @param arg - second argument of additional operation
     *
     * @return result of additional operation.
     */
    public NumberListImpl additionalOperation(NumberList arg) {
        if (arg == null) {
            return null;
        }

        BigInteger a = this.toBigInteger();
        BigInteger b;

        if (arg instanceof NumberListImpl) {
            b = ((NumberListImpl) arg).toBigInteger();
        } else {
            BigInteger baseBI = BigInteger.valueOf(this.base);
            b = BigInteger.ZERO;
            for (Byte d : arg) {
                if (d == null) {
                    continue;
                }
                b = b.multiply(baseBI).add(BigInteger.valueOf(d));
            }
        }

        int c7 = Math.floorMod(getRecordBookNumber(), 7);
        BigInteger res;

        switch (c7) {
            case 4:
                if (b.equals(BigInteger.ZERO)) {
                    res = BigInteger.ZERO;
                } else {
                    res = a.remainder(b);
                }
                break;
            case 0:
                res = a.add(b);
                break;
            case 1:
                res = a.subtract(b);
                if (res.signum() < 0) {
                    res = BigInteger.ZERO;
                }
                break;
            case 2:
                res = a.multiply(b);
                break;
            case 3:
                if (b.equals(BigInteger.ZERO)) {
                    res = BigInteger.ZERO;
                } else {
                    res = a.divide(b);
                }
                break;
            case 5:
                res = a.and(b);
                break;
            case 6:
                res = a.or(b);
                break;
            default:
                res = BigInteger.ZERO;
        }

        int mainBase = getMainBase();
        return new NumberListImpl(mainBase, res);
    }


    /**
     * Returns string representation of number, stored in the list
     * in <b>decimal</b> scale of notation.
     *
     * @return string representation in <b>decimal</b> scale.
     */
    public String toDecimalString() {
        BigInteger value = toBigInteger();
        return value.toString();
    }


    @Override
    public String toString() {
        if (isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(size);
        Node cur = head;
        for (int i = 0; i < size; i++) {
            int d = cur.value & 0xFF;
            char ch;
            if (d < 10) {
                ch = (char) ('0' + d);
            } else {
                ch = (char) ('A' + (d - 10));
            }
            sb.append(ch);
            cur = cur.next;
        }
        return sb.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof List)) {
            return false;
        }
        List<?> other = (List<?>) o;
        if (this.size != other.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            Object ov = other.get(i);
            Byte tv = get(i);
            if (!(ov instanceof Byte)) {
                return false;
            }
            if (!tv.equals(ov)) {
                return false;
            }
        }
        return true;
    }

    private void clearInternal() {
        head = null;
        tail = null;
        size = 0;
    }

    private void linkLast(byte value) {
        Node node = new Node(value);
        if (head == null) {
            head = tail = node;
            head.next = head;
            head.prev = head;
        } else {
            node.prev = tail;
            node.next = head;
            tail.next = node;
            head.prev = node;
            tail = node;
        }
        size++;
    }

    private Node node(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        Node cur;
        if (index <= size / 2) {
            cur = head;
            for (int i = 0; i < index; i++) {
                cur = cur.next;
            }
        } else {
            cur = tail;
            for (int i = size - 1; i > index; i--) {
                cur = cur.prev;
            }
        }
        return cur;
    }

    private void initFromDecimalString(String value) {
        clearInternal();
        if (value == null) {
            return;
        }
        String v = value.trim();
        if (v.isEmpty()) {
            return;
        }
        if (v.startsWith("-")) {
            return;
        }
        if (!v.matches("\\d+")) {
            return;
        }
        BigInteger number = new BigInteger(v);
        initFromBigInteger(number);
    }

    private void initFromBigInteger(BigInteger number) {
        clearInternal();
        if (number == null || number.signum() <= 0) {
            return;
        }
        BigInteger b = BigInteger.valueOf(base);
        List<Byte> digits = new ArrayList<>();
        BigInteger n = number;

        while (n.signum() > 0) {
            BigInteger[] dr = n.divideAndRemainder(b);
            byte digit = dr[1].byteValue();
            digits.add(digit);
            n = dr[0];
        }

        Collections.reverse(digits);
        for (Byte d : digits) {
            linkLast(d);
        }
    }

    private BigInteger toBigInteger() {
        if (isEmpty()) {
            return BigInteger.ZERO;
        }
        BigInteger result = BigInteger.ZERO;
        BigInteger b = BigInteger.valueOf(base);
        Node cur = head;
        for (int i = 0; i < size; i++) {
            result = result.multiply(b).add(BigInteger.valueOf(cur.value));
            cur = cur.next;
        }
        return result;
    }

    private void loadFromFile(File file) {
        clearInternal();
        if (file == null || !file.exists() || !file.isFile()) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            initFromDecimalString(line);
        } catch (IOException ignored) {
        }
    }


    @Override
    public int size() {
        return size;
    }


    @Override
    public boolean isEmpty() {
        return size == 0;
    }


    @Override
    public boolean contains(Object o) {
        if (!(o instanceof Byte)) {
            return false;
        }
        byte val = (Byte) o;
        Node cur = head;
        for (int i = 0; i < size; i++) {
            if (cur.value == val) {
                return true;
            }
            cur = cur.next;
        }
        return false;
    }


    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {
            private Node current = head;
            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public Byte next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                byte val = current.value;
                current = current.next;
                index++;
                return val;
            }
        };
    }


    @Override
    public Object[] toArray() {
        Byte[] arr = new Byte[size];
        for (int i = 0; i < size; i++) {
            arr[i] = get(i);
        }
        return arr;
    }


    @Override
    public <T> T[] toArray(T[] a) {
        int s = size;
        @SuppressWarnings("unchecked")
        T[] r = a.length >= s
            ? a
            : (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), s);
        for (int i = 0; i < s; i++) {
            @SuppressWarnings("unchecked")
            T val = (T) get(i);
            r[i] = val;
        }
        if (r.length > s) {
            r[s] = null;
        }
        return r;
    }


    @Override
    public boolean add(Byte e) {
        if (e == null) {
            return false;
        }
        if (e < 0 || e >= base) {
            throw new IllegalArgumentException("Digit " + e + " is not valid for base " + base);
        }
        linkLast(e);
        return true;
    }


    @Override
    public boolean remove(Object o) {
        if (!(o instanceof Byte)) {
            return false;
        }
        byte val = (Byte) o;
        if (isEmpty()) {
            return false;
        }
        Node cur = head;
        for (int i = 0; i < size; i++) {
            if (cur.value == val) {
                unlinkNode(cur);
                return true;
            }
            cur = cur.next;
        }
        return false;
    }

    private void unlinkNode(Node node) {
        if (size == 1) {
            clearInternal();
            return;
        }
        Node prev = node.prev;
        Node next = node.next;
        prev.next = next;
        next.prev = prev;
        if (node == head) {
            head = next;
        }
        if (node == tail) {
            tail = prev;
        }
        size--;
    }


    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }


    @Override
    public boolean addAll(Collection<? extends Byte> c) {
        boolean modified = false;
        for (Byte b : c) {
            if (add(b)) {
                modified = true;
            }
        }
        return modified;
    }


    @Override
    public boolean addAll(int index, Collection<? extends Byte> c) {
        int i = index;
        for (Byte b : c) {
            add(i++, b);
        }
        return !c.isEmpty();
    }


    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object o : c) {
            while (remove(o)) {
                modified = true;
            }
        }
        return modified;
    }


    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<Byte> it = iterator();
        while (it.hasNext()) {
            Byte b = it.next();
            if (!c.contains(b)) {
                remove(b);
                modified = true;
            }
        }
        return modified;
    }


    @Override
    public void clear() {
        clearInternal();
    }


    @Override
    public Byte get(int index) {
        return node(index).value;
    }


    @Override
    public Byte set(int index, Byte element) {
        Node n = node(index);
        byte old = n.value;
        n.value = element;
        return old;
    }


    @Override
    public void add(int index, Byte element) {
        if (index == size) {
            add(element);
            return;
        }
        Node current = node(index);
        Node newNode = new Node(element);
        Node prev = current.prev;

        newNode.next = current;
        newNode.prev = prev;
        prev.next = newNode;
        current.prev = newNode;

        if (index == 0) {
            head = newNode;
        }
        size++;
    }


    @Override
    public Byte remove(int index) {
        Node n = node(index);
        byte old = n.value;
        unlinkNode(n);
        return old;
    }


    @Override
    public int indexOf(Object o) {
        if (!(o instanceof Byte)) {
            return -1;
        }
        byte val = (Byte) o;
        Node cur = head;
        for (int i = 0; i < size; i++) {
            if (cur.value == val) {
                return i;
            }
            cur = cur.next;
        }
        return -1;
    }


    @Override
    public int lastIndexOf(Object o) {
        if (!(o instanceof Byte)) {
            return -1;
        }
        byte val = (Byte) o;
        Node cur = tail;
        for (int i = size - 1; i >= 0; i--) {
            if (cur.value == val) {
                return i;
            }
            cur = cur.prev;
        }
        return -1;
    }


    @Override
    public ListIterator<Byte> listIterator() {
        throw new UnsupportedOperationException("listIterator not implemented");
    }


    @Override
    public ListIterator<Byte> listIterator(int index) {
        throw new UnsupportedOperationException("listIterator(int) not implemented");
    }


    @Override
    public List<Byte> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("subList not implemented");
    }


    @Override
    public boolean swap(int index1, int index2) {
        if (index1 < 0 || index1 >= size || index2 < 0 || index2 >= size) {
            return false;
        }
        if (index1 == index2) {
            return true;
        }
        Node n1 = node(index1);
        Node n2 = node(index2);
        byte tmp = n1.value;
        n1.value = n2.value;
        n2.value = tmp;
        return true;
    }


    @Override
    public void sortAscending() {
        if (size <= 1) {
            return;
        }
        Byte[] arr = new Byte[size];
        for (int i = 0; i < size; i++) {
            arr[i] = get(i);
        }
        Arrays.sort(arr);
        Node cur = head;
        for (int i = 0; i < size; i++) {
            cur.value = arr[i];
            cur = cur.next;
        }
    }


    @Override
    public void sortDescending() {
        if (size <= 1) {
            return;
        }
        Byte[] arr = new Byte[size];
        for (int i = 0; i < size; i++) {
            arr[i] = get(i);
        }
        Arrays.sort(arr, Collections.reverseOrder());
        Node cur = head;
        for (int i = 0; i < size; i++) {
            cur.value = arr[i];
            cur = cur.next;
        }
    }


    @Override
    public void shiftLeft() {
        if (size <= 1) {
            return;
        }
        head = head.next;
        tail = tail.next;
    }


    @Override
    public void shiftRight() {
        if (size <= 1) {
            return;
        }
        head = head.prev;
        tail = tail.prev;
    }
}
