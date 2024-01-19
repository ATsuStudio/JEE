package com.atsustudio.Interfaces;
import java.util.List;
import java.util.Map;

public interface CRUDInterface<T> {
	public T find(int id);
	public List<T> all();
	public List<T> where(String column, String value);
	public boolean delete(int id);
	public T update(Map<String, Object> object);
	public T save();
}
