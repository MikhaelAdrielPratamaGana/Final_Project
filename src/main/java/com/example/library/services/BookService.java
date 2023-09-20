package com.example.library.services;

import com.example.library.dto.request.BookRequest;
import com.example.library.model.Book;
import com.example.library.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    // Fungsi untuk menampilkan seluruh data buku
    public List<Book> bookList() {
        return bookRepository.findAll();
    }

    // Fungsi untuk menampilkan data buku berdasarkan id
    public Book getBookById(long id) {
        Optional<Book> book = bookRepository.findById(id);

        if (book.isPresent()) {
            return book.get();
        } else {
            return null;
        }
    }

    // Fungsi untuk menambah data buku
    public Book addBook(BookRequest request) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        if (isEmptyOrSpace(request.getTitle()) || isEmptyOrSpace(request.getAuthor())
                || isEmptyOrSpace(request.getCategory())
                || isEmptyOrSpace(request.getPublisher())
                || isEmptyOrSpace(request.getIsbn()) || request.getStock() == null
                || request.getStock() < 0 || Integer.parseInt(request.getYear()) < 0
                || Integer.parseInt(request.getYear()) > currentYear
                || !isAlphaNumericWithSpaces(request.getAuthor())) {
            return null;
        }

        if (!isValidIsbn(request.getIsbn())) {
            return null;
        }

        if (isIsbnExists(request.getIsbn())) {
            return null;
        }

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setCategory(request.getCategory());
        book.setPublisher(request.getPublisher());
        book.setYear(request.getYear());
        book.setIsbn(request.getIsbn());
        book.setStock(request.getStock());

        return bookRepository.save(book);
    }

    // Fungsi untuk mengupdate data buku
    public boolean updateBook(Long id, BookRequest request) {
        Optional<Book> book = bookRepository.findById(id);

        if (!book.isPresent()) {
            return false;
        } else {
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            if (isEmptyOrSpace(request.getTitle()) || isEmptyOrSpace(request.getAuthor())
                    || isEmptyOrSpace(request.getCategory())
                    || isEmptyOrSpace(request.getPublisher())
                    || isEmptyOrSpace(request.getIsbn()) || request.getStock() == null
                    || request.getStock() < 0 || Integer.parseInt(request.getYear()) < 0
                    || Integer.parseInt(request.getYear()) > currentYear
                    || !isAlphaNumericWithSpaces(request.getAuthor())) {
                return false;
            }

            book.get().setTitle(request.getTitle());
            book.get().setAuthor(request.getAuthor());
            book.get().setCategory(request.getCategory());
            book.get().setPublisher(request.getPublisher());
            book.get().setYear(request.getYear());
            book.get().setIsbn(request.getIsbn());
            book.get().setStock(request.getStock());
            bookRepository.save(book.get());
            return true;
        }
    }

    // Fungsi untuk delete data buku
    public boolean deleteBook(Long id) {
        Optional<Book> book = bookRepository.findById(id);

        if (!book.isPresent()) {
            return false;
        } else {
            book.get().setDeletedAt(new Date());
            bookRepository.save(book.get());
            return true;
        }
    }

    private boolean isEmptyOrSpace(String str) {
        return str == null || str.trim().isEmpty();
    }

    private boolean isValidIsbn(String isbn) {
        return isbn.matches("^\\d{13}$");
    }

    private boolean isAlphaNumericWithSpaces(String str) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9\\s]*$");
        return pattern.matcher(str).matches();
    }

    private boolean isIsbnExists(String isbn) {
        List<Book> existingBooks = bookRepository.findByIsbn(isbn);
        return !existingBooks.isEmpty();
    }
}
