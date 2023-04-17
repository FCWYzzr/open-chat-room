package toolkit.protocol;


import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public interface FriendFactory
        extends Iterable<Friend>{
    Iterator<Friend> emptyIterator
            = new Iterator<>() {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Friend next() {
            return null;
        }
    };

    class defaultLoader<protoT>
            implements Iterator<Friend>{
        final Iterator<protoT> iter;
        final Set<protoT> records;
        final Function<protoT, Optional<Friend>> builder;

        Friend friend;

        public defaultLoader(
                Set<protoT> toBeLoad,
                Set<protoT> beingLoad,
                Function<protoT, Optional<Friend>> mapper) {

            iter = toBeLoad.iterator();
            records = beingLoad;
            builder = mapper;
            friend = null;
        }

        @Override
        public boolean hasNext() {
            protoT current;
            Optional<Friend> buffer;

            while(iter.hasNext()){
                current = iter.next();

                if (records.contains(current))
                    continue;

                buffer = builder.apply(current);

                if (buffer.isEmpty())
                    continue;

                friend = buffer.get();
                if (! friend.launch())
                    continue;

                records.add(current);
                return true;
            }
            return false;
        }

        @Override
        public Friend next() {
            return friend;
        }
    }

    void doRecycle(Friend instance);
}
