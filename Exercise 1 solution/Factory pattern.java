abstract class Document {
    public abstract void create();
}

class PDFDocument extends Document {
    @Override
    public void create() {
        System.out.println("Creating PDF document");
    }
}

class WordDocument extends Document {
    @Override
    public void create() {
        System.out.println("Creating Word document");
    }
}

abstract class DocumentCreator {
    public abstract Document createDocument();

    public void generateDocument() {
        Document doc = createDocument();
        doc.create();
    }
}

class PDFCreator extends DocumentCreator {
    @Override
    public Document createDocument() {
        return new PDFDocument();
    }
}

class WordCreator extends DocumentCreator {
    @Override
    public Document createDocument() {
        return new WordDocument();
    }
}

public class DocumentDemo {
    public static void main(String[] args) {
        DocumentCreator pdfCreator = new PDFCreator();
        pdfCreator.generateDocument();

        DocumentCreator wordCreator = new WordCreator();
        wordCreator.generateDocument();
    }
}