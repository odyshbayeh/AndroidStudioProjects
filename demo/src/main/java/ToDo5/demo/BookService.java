package ToDo5.demo;

import ToDo5.demo.Book;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    private final List<Book> bookList = new ArrayList<>();

    public List<Book> getAllBooks() {
        return bookList;
    }

    public String addBook(Book book) {
        bookList.add(book);
        return "Book added successfully";
    }

    public Book updateBook(Integer id, Book updatedBook) {
        for (Book book : bookList) {
            if (book.getId() == id) {
                book.setTitle(updatedBook.getTitle());
                book.setAuthor(updatedBook.getAuthor());
                return book;
            }
        }
        return null;
    }

    public String deleteBook(Integer id) {
        bookList.removeIf(book -> book.getId() == id);
        return "Book deleted successfully";
    }

    public Book getBookByTitle(String title) {
        return bookList.stream()
                .filter(book -> book.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElse(null);
    }
}

