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
   width="136.57201mm"
   height="136.5715mm"
   viewBox="0 0 136.572 136.5715"
   version="1.1"
   id="svg1"
   xmlns="http://www.w3.org/2000/svg"
   xmlns:svg="http://www.w3.org/2000/svg">
  <defs
     id="defs1" />
  <g
     id="layer1"
     transform="translate(-36.714246,-80.214246)">
    <circle
       :style="{fill:'none',stroke:color,strokeWidth:strokeWidth,strokeLinecap:'round',strokeLinejoin:'round',strokeOpacity:1}"
       id="path1"
       cx="105"
       cy="148.5"
       r="64.285759" />
    <circle
       :style="{fill:'none',stroke:color,strokeWidth:strokeWidth,strokeLinecap:'round',strokeLinejoin:'round',strokeOpacity:1}"
       id="path1-2"
       cx="105"
       cy="130.44272"
       r="26.867472" />
    <path
       :style="{fill:'none',stroke:color,strokeWidth:strokeWidth,strokeLinecap:'round',strokeLinejoin:'round',strokeOpacity:1}"
       id="path1-2-7"
       d="m 57.019158,189.83067 a 53.21838,53.21838 0 0 1 47.918422,-32.01821 53.21838,53.21838 0 0 1 48.97184,30.38255" />
  </g>
</svg>
</template>
