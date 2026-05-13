<script setup>
import { computed } from 'vue';

const props = defineProps({
  closed: {type: Boolean, default: false},
  strokeWidth: { type: Number, default: 1 },
  color: { type: String, default: '#000000' },
});
const strokeWidth = computed(()=>8*props.strokeWidth);
</script>

<template>
<svg
   v-if="!props.closed"
   v-bind="$attrs"
   width="128mm"
   height="128mm"
   viewBox="0 0 128 128"
   version="1.1"
   id="svg1"
   xmlns="http://www.w3.org/2000/svg"
   xmlns:svg="http://www.w3.org/2000/svg">
  <defs
     id="defs1" />
  <g
     id="layer1"
     transform="translate(-41.237731,-61.078356)">
    <g
       id="g3"
       transform="translate(6.077178,29.244889)">
      <path
         :style="{fill:'none',stroke:color,strokeWidth:strokeWidth,strokeLinecap:'round',strokeLinejoin:'round',strokeOpacity:1}"
         d="m 45.305571,98.437072 c 37.272125,-44.45387 72.672649,-44.502713 107.777809,0 M 45.237726,98.255031 C 81.252259,135.4819 117.38703,136.50007 153.01554,98.4921"
         id="path2" />
      <circle
         :style="{fill:'none',stroke:color,strokeWidth:strokeWidth,strokeLinecap:'round',strokeLinejoin:'round',strokeOpacity:1}"
         id="path3"
         r="9.5554028"
         cy="96.808777"
         cx="100.4765" />
    </g>
  </g>
</svg>
<svg
   v-if="props.closed"
   v-bind="$attrs"
   width="128mm"
   height="128mm"
   viewBox="0 0 128 128"
   version="1.1"
   id="svg1"
   xmlns="http://www.w3.org/2000/svg"
   xmlns:svg="http://www.w3.org/2000/svg">
  <defs
     id="defs1" />
  <g
     id="layer1"
     transform="translate(-41.237731,-61.078356)">
    <g
       id="g3"
       transform="translate(6.077178,29.244889)">
      <path
         :style="{fill:'none',stroke:color,strokeWidth:strokeWidth,strokeLinecap:'round',strokeLinejoin:'round',strokeOpacity:1}"
         d="m 45.305571,98.437072 c 37.272125,-44.45387 72.672649,-44.502713 107.777809,0 M 45.237726,98.255031 C 81.252259,135.4819 117.38703,136.50007 153.01554,98.4921"
         id="path2" />
      <path
         :style="{fill:'none',stroke:color,strokeWidth:strokeWidth,strokeLinecap:'round',strokeLinejoin:'round',strokeOpacity:1}"
         id="path3-2"
         d="m -79.663378,107.10447 a 9.5554028,9.5554028 0 0 1 -6.680792,9.11276"
         transform="rotate(-86.632997)" />
      <path
         :style="{fill:'none',stroke:color,strokeWidth:strokeWidth,strokeLinecap:'round',strokeLinejoin:'round',strokeOpacity:1}"
         id="path3-2-9"
         d="m -71.367716,111.63689 a 9.5554028,9.5554028 0 0 1 -6.680792,9.11275"
         transform="matrix(-0.98568856,0.16857659,0.16857659,0.98568856,0,0)" />
      <path
         :style="{fill:'none',stroke:color,strokeWidth:strokeWidth,strokeLinecap:'round',strokeLinejoin:'round',strokeOpacity:1}"
         d="M 58.887078,57.692713 143.78399,136.42171"
         id="path4" />
    </g>
  </g>
</svg>
</template>