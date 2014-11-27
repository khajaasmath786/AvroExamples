package com.cts.avro;

import java.io.File;
import java.io.IOException;
 

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import com.cts.avro.entity.Book;
 
 
public class FileSerializationExample {
	public static void main(final String[] args) throws IOException {
		Book book1 = Book.newBuilder().setId(123).setName("Programming is fun")
				.setCategory("Fiction").build();
		Book book2 = new Book("Some book", 456, "Horror");
		Book book3 = new Book();
		book3.setName("And another book");
		book3.setId(789);
		File store = File.createTempFile("book", ".avro");
 
		// serializing
		System.out
				.println("serializing books to temp file: " + store.getPath());
		DatumWriter<Book> bookDatumWriter = new SpecificDatumWriter<Book>(
				Book.class);
		DataFileWriter<Book> bookFileWriter = new DataFileWriter<Book>(
				bookDatumWriter);
		bookFileWriter.create(book1.getSchema(), store);
		bookFileWriter.append(book1);
		bookFileWriter.append(book2);
		bookFileWriter.append(book3);
		bookFileWriter.close();
 
		// deserializing
		DatumReader<Book> bookDatumReader = new SpecificDatumReader<Book>(
				Book.class);
		DataFileReader<Book> bookFileReader = new DataFileReader<Book>(store,
				bookDatumReader);
		while (bookFileReader.hasNext()) {
			Book b1 = bookFileReader.next();
			System.out.println("deserialized from file: " + b1);
		}
	}
 
}
