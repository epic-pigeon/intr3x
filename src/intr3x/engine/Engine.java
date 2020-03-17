package intr3x.engine;

import kotlin.Pair;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class Engine {
    private class EngineCanvas extends JPanel {
        EngineCanvas(int h, int w) {
            setBackground(Color.BLACK);
            setSize(w, h);
        }

        protected void paintComponent(Graphics graphics) {
            graphics.drawImage(image, 0, 0, this);
        }
    }

    protected final BufferedImage image;
    private final EngineCanvas engineCanvas;
    protected final int height, width;
    private final Frame frame;
    private final Thread updateThread;
    protected static final Logger logger = Logger.getLogger(Engine.class);
    private boolean paused = false;
    private Graphics2D graphics = null;
    private List<Pair<KeyEventFilter, Consumer<KeyEvent>>> keyTypedListeners = new ArrayList<>();
    private List<Pair<KeyEventFilter, Consumer<KeyEvent>>> keyPressedListeners = new ArrayList<>();
    private List<Pair<KeyEventFilter, Consumer<KeyEvent>>> keyReleasedListeners = new ArrayList<>();
    protected double maxFPS = 1;

    public Engine(int h, int w) {
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < w; i++) for (int j = 0; j < h; j++) image.setRGB(i, j, 0);
        engineCanvas = new EngineCanvas(h, w);
        this.height = h; this.width = w;

        try {
            onCreate();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred when starting the engine", e);
        }

        frame = new Frame();
        frame.setSize(w, h);
        frame.add(engineCanvas);
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                for (Pair<KeyEventFilter, Consumer<KeyEvent>> listener: keyTypedListeners) {
                    if (listener.getFirst().keyEventSuits(e)) listener.getSecond().accept(e);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                for (Pair<KeyEventFilter, Consumer<KeyEvent>> listener: keyPressedListeners) {
                    if (listener.getFirst().keyEventSuits(e)) listener.getSecond().accept(e);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                for (Pair<KeyEventFilter, Consumer<KeyEvent>> listener: keyReleasedListeners) {
                    if (listener.getFirst().keyEventSuits(e)) listener.getSecond().accept(e);
                }
            }
        });

        updateThread = new Thread(() -> {
            double diff = 0;
            while (true) {
                if (!paused) {
                    long nanos = System.nanoTime();
                    try {
                        onUpdate(diff);
                    } catch (Exception e) {
                        throw new RuntimeException("Error occurred when updating", e);
                    }
                    engineCanvas.repaint();
                    diff = (double)(System.nanoTime() - nanos) / 1000000000;
                    if (maxFPS > 0 && diff < 1 / maxFPS) {
                        try {
                            Thread.sleep((long)((1 / maxFPS - diff) * 1000));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        diff = (double)(System.nanoTime() - nanos) / 1000000000;
                    }
                }
            }
        });
    }

    public double getMaxFPS() {
        return maxFPS;
    }

    public void setMaxFPS(double maxFPS) {
        this.maxFPS = maxFPS;
    }

    public void start() {
        updateThread.start();
        frame.setVisible(true);
    }

    public void pause() {
        paused = true;
    }

    public void unpause() {
        paused = false;
    }

    public void stop() {
        updateThread.stop();
        frame.dispose();
        if (graphics != null) {
            graphics.dispose();
        }
    }

    public void fill(int x, int y, int h, int w, Color color) {
        Color save = image.getGraphics().getColor();
        image.getGraphics().setColor(color);
        image.getGraphics().fillRect(x, y, w, h);
        image.getGraphics().setColor(save);
    }

    public Graphics2D getGraphics() {
        if (graphics == null) {
            graphics = image.createGraphics();
        }
        return graphics;
    }

    public interface KeyEventFilter {
        boolean keyEventSuits(KeyEvent event);
    }

    public static final class CharArrayKeyEventFilter implements KeyEventFilter {
        char[] chars;

        public CharArrayKeyEventFilter(char[] chars) {
            this.chars = chars;
        }

        @Override
        public boolean keyEventSuits(KeyEvent event) {
            char keyChar = event.getKeyChar();
            for (char aChar : chars) {
                if (keyChar == aChar) return true;
            }
            return false;
        }
    }

    public static final class KeyCodesKeyEventFilter implements KeyEventFilter {
        int[] codes;

        public KeyCodesKeyEventFilter(int[] codes) {
            this.codes = codes;
        }

        @Override
        public boolean keyEventSuits(KeyEvent event) {
            int keyCode = event.getKeyCode();
            for (int code : codes) {
                if (code == keyCode) return true;
            }
            return false;
        }
    }

    public static final class AnyKeyEventFilter implements KeyEventFilter {
        @Override
        public boolean keyEventSuits(KeyEvent event) {
            return true;
        }
    }

    public void addKeyTypedListener(Consumer<KeyEvent> action) {
        keyTypedListeners.add(new Pair<>(new AnyKeyEventFilter(), action));
    }

    public void addKeyTypedListener(Consumer<KeyEvent> action, char... chars) {
        keyTypedListeners.add(new Pair<>(new CharArrayKeyEventFilter(chars), action));
    }

    public void addKeyTypedListener(Consumer<KeyEvent> action, int... codes) {
        keyTypedListeners.add(new Pair<>(new KeyCodesKeyEventFilter(codes), action));
    }

    public void addKeyTypedListener(Consumer<KeyEvent> action, KeyEventFilter keyEventFilter) {
        keyTypedListeners.add(new Pair<>(keyEventFilter, action));
    }

    public void addKeyPressedListener(Consumer<KeyEvent> action) {
        keyPressedListeners.add(new Pair<>(new AnyKeyEventFilter(), action));
    }

    public void addKeyPressedListener(Consumer<KeyEvent> action, char... chars) {
        keyPressedListeners.add(new Pair<>(new CharArrayKeyEventFilter(chars), action));
    }

    public void addKeyPressedListener(Consumer<KeyEvent> action, int... codes) {
        keyPressedListeners.add(new Pair<>(new KeyCodesKeyEventFilter(codes), action));
    }

    public void addKeyPressedListener(Consumer<KeyEvent> action, KeyEventFilter keyEventFilter) {
        keyPressedListeners.add(new Pair<>(keyEventFilter, action));
    }

    public void addKeyReleasedListener(Consumer<KeyEvent> action) {
        keyReleasedListeners.add(new Pair<>(new AnyKeyEventFilter(), action));
    }

    public void addKeyReleasedListener(Consumer<KeyEvent> action, char... chars) {
        keyReleasedListeners.add(new Pair<>(new CharArrayKeyEventFilter(chars), action));
    }

    public void addKeyReleasedListener(Consumer<KeyEvent> action, int... codes) {
        keyReleasedListeners.add(new Pair<>(new KeyCodesKeyEventFilter(codes), action));
    }

    public void addKeyReleasedListener(Consumer<KeyEvent> action, KeyEventFilter keyEventFilter) {
        keyReleasedListeners.add(new Pair<>(keyEventFilter, action));
    }

    abstract protected void onCreate() throws Exception;
    abstract protected void onStart() throws Exception;
    abstract protected void onUpdate(double timeElapsed) throws Exception;
}
