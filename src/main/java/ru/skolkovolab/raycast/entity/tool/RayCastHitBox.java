package ru.skolkovolab.raycast.entity.tool;

import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.jetbrains.annotations.NotNull;
import ru.skolkovolab.raycast.entity.HitBox;
import ru.skolkovolab.raycast.entity.HitBoxGroup;
import ru.skolkovolab.raycast.shared.RayCastIterator;
import ru.skolkovolab.raycast.shared.collision.HitBoxCollision;

import java.util.*;

/**
 * @author sidey383
 */
public class RayCastHitBox<T extends HitBox> implements Iterable<HitBoxCollision<T>> {

    private final List<RayCastHitBoxGroup<T>> groups;

    private RayCastHitBox(List<RayCastHitBoxGroup<T>> groups) {
        this.groups = Collections.unmodifiableList(groups);
    }

    public static <T extends HitBox> RayCastHitBox<T> createRaycast(Collection<HitBoxGroup<T>> hitboxes, Vector3D pos, Vector3D dir) {
        dir = dir.normalize();
        Vector3D finalDir = dir;
        List<RayCastHitBoxGroup<T>> groups = hitboxes.stream()
                .map(h -> RayCastHitBoxGroup.createGroup(h, pos, finalDir))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted()
                .toList();
        return new RayCastHitBox<>(groups);
    }

    @NotNull
    @Override
    public Iterator<HitBoxCollision<T>> iterator() {
        return new RayCastHitboxIterator(groups);
    }

    private class RayCastHitboxIterator implements RayCastIterator<HitBoxCollision<T>> {

        /**
         * Текущие итераторы, отсортированы по расстоянию до текущего элемента.
         * current всегда возвращает значение.
         **/
        private final ArrayList<RayCastIterator<HitBoxCollision<T>>> currentIterators = new ArrayList<>();

        /**
         * Доступные группы.
         * Отсортированы по ближайшему входу (возможно, фактическое пересечение будет дальше или его не будет)
         **/
        private final Deque<RayCastHitBoxGroup<T>> availableGroups;

        private HitBoxCollision<T> current = null;

        public RayCastHitboxIterator(Collection<RayCastHitBoxGroup<T>> available) {
            this.availableGroups = new ArrayDeque<>(available);
        }

        @Override
        public boolean hasNext() {
            if (!currentIterators.isEmpty())
                return true;
            while (!availableGroups.isEmpty()) {
                var i = availableGroups.removeFirst().iterator();
                if (i.hasNext()) {
                    i.next();
                    RayCastHitBoxUtils.sortedInsert(currentIterators, i);
                    return true;
                }
            }
            return false;
        }

        @Override
        public HitBoxCollision<T> next() {
            RayCastIterator<HitBoxCollision<T>> selected = null;
            double selectedDist = Double.MAX_VALUE;
            int selectedPosition = -1;
            if (!currentIterators.isEmpty()) {
                selected = currentIterators.get(0);
                selectedPosition = 0;
                selectedDist = selected.distance();
            }
            while (!availableGroups.isEmpty() && availableGroups.getFirst().inlet() < selectedDist) {
                var i = availableGroups.removeFirst().iterator();
                if (!i.hasNext())
                    continue;
                currentIterators.add(i);
                double dist = i.next().distance();
                if (dist < selectedDist) {
                    selectedDist = dist;
                    selectedPosition = currentIterators.size() - 1;
                    selected = i;
                }
            }
            if (selected == null)
                throw new NoSuchElementException();
            current = selected.current();
            if (!selected.hasNext()) {
                currentIterators.remove(selectedPosition);
            } else {
                selected.next();
            }
            currentIterators.sort(RayCastIterator::compareTo);
            return current;
        }

        @Override
        public double distance() {
            if (current == null)
                return -1;
            return current.distance();
        }

        @Override
        public HitBoxCollision<T> current() {
            if (current == null)
                throw new NoSuchElementException();
            return current;
        }

        @Override
        public boolean hasCurrent() {
            return current != null;
        }
    }

}
